package io.github.transfusion.deployapp.storagemanagementservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.dto.request.GenerateAssetRequest;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.dto.response.GenerateAssetResult;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterCriteria;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterSpecification;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryTypeSpecification;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapper;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapper;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import io.github.transfusion.deployapp.storagemanagementservice.services.assets.Constants;
import io.github.transfusion.deployapp.storagemanagementservice.services.assets.IPAAssetsService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.NotImplementedException;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryService.IDENTIFIER_TO_CLASS_NAME;
import static org.springframework.data.jpa.domain.Specification.where;


@RestController
@RequestMapping("/api/v1/app")
public class AppController {

    Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    @GetMapping("credtest")
    public ResponseEntity<String> blah(@RequestParam UUID id) throws JsonProcessingException {
        storageCredsUpdateService.getCredential(id, Instant.now()); // no matter what will fetch from db then
        return ResponseEntity.ok("ok");
    }

    @Autowired
    private AppBinaryService appBinaryService;

    @Autowired
    private AppBinaryMapper appBinaryMapper;

    /**
     * Creates a new binary by autodetecting its type
     *
     * @param storageCredentialId {@link java.util.UUID}
     * @param binary              multipart form-data file upload field
     * @param credentialCreatedOn
     * @return {@link ResponseEntity<AppBinaryDTO>}
     * @throws IOException in the event that file operations fail
     */
    @RequestMapping(
            path = "/binary",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AppBinaryDTO> detectAndStoreBinary(@RequestParam(required = false) String organizationId,
                                                             @RequestParam UUID storageCredentialId,
                                                             @RequestParam("binary") MultipartFile binary,
                                                             @RequestParam Instant credentialCreatedOn) throws IOException {
//        https://www.baeldung.com/spring-multipartfile-to-file
        File tempFile = File.createTempFile("binary", binary.getOriginalFilename());
        binary.transferTo(tempFile);
        // TODO: handle organization
        AppBinary appBinary = appBinaryService.detectAndStoreOwnBinary(storageCredentialId, credentialCreatedOn, tempFile);
        tempFile.delete();
        return new ResponseEntity<>(appBinaryMapper.toDTO(appBinary), HttpStatus.CREATED);
    }

    @GetMapping("/binary")
    @Operation(summary = "Gets all app binaries which you have access to", description = "searchKey, searchOperation, and searchValue must all be the same length or null")
    public Page<AppBinaryDTO> list(@RequestParam(required = false) String organizationId,
                                   @RequestParam(required = false) List<String> searchKey,
                                   @RequestParam(required = false) List<String> searchOperation,
                                   @RequestParam(required = false) List<String> searchValue,
                                   @RequestParam(required = false) List<String> types,
                                   Pageable page) {

        int searchKeySize = searchKey == null ? 0 : searchKey.size();
        int searchOperationSize = searchOperation == null ? 0 : searchOperation.size();
        int searchValueSize = searchValue == null ? 0 : searchValue.size();

        if (!(searchKeySize == searchOperationSize && searchOperationSize == searchValueSize))
            throw new IllegalArgumentException("invalid search criteria");

        // TODO: handle organization

        Specification<AppBinary> appBinarySpecification = where(null);
        for (int i = 0; i < searchKeySize; i++) {
            appBinarySpecification = appBinarySpecification.and(new AppBinaryFilterSpecification(new AppBinaryFilterCriteria(searchKey.get(i),
                    searchOperation.get(i), searchValue.get(i))));
        }

        if (types != null) {
            List<Class<? extends AppBinary>> classes = types.stream().map(id -> IDENTIFIER_TO_CLASS_NAME.get(id)).collect(Collectors.toList());
            Specification<AppBinary> appBinaryTypeSpecification = where(null);
            for (Class<? extends AppBinary> c : classes)
                appBinaryTypeSpecification = appBinaryTypeSpecification.or(new AppBinaryTypeSpecification(c));
            appBinarySpecification = appBinarySpecification.and(appBinaryTypeSpecification);
        }

        if (organizationId == null)
            return appBinaryService.findOwnPaginated(appBinarySpecification, page);

        return null;
    }

    @GetMapping("/binary/{id}")
    @PreAuthorize("hasPermission(#id, 'APPBINARY_EDIT')")
    public ResponseEntity<AppBinaryDTO> getAppBinaryById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(appBinaryService.getAppBinaryById(id), HttpStatus.OK);
    }

    /* asset-related endpoints go below */

    @Autowired
    private IPAAssetsService ipaAssetsService;

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private StorageProvider storageProvider;

    @PreAuthorize("hasPermission(#id, 'APPBINARY_EDIT')")
    @PostMapping("/binary/{id}/generateAsset")
    public GenerateAssetResult generateAsset(@PathVariable("id") UUID id,
                                             @RequestBody GenerateAssetRequest request) {
        if (request.getType().equals(Constants.IPA_ASSETS.MOBILEPROVISION.toString())) {
//            JobId jobId = jobScheduler.<IPAAssetsService>enqueue(svc -> {
//                AppBinaryAsset appBinaryAsset = svc.generateIPAMobileProvision(id);
//                logger.info("mobileprovision gen done " + appBinaryAsset.getId());
//            });
            JobId jobId = jobScheduler.enqueue(() -> ipaAssetsService.generateIPAMobileProvision(id));
//            logger.info("mobileprovision gen done " + appBinaryAsset.getId());
            return new GenerateAssetResult(jobId.asUUID());
        }
        throw new NotImplementedException(String.format("Generating Assets of type %s is not implemented yet", request.getType()));
    }



}
