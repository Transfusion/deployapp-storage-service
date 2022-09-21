package io.github.transfusion.deployapp.storagemanagementservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapper;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/app")
public class AppController {

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
     * @param storageCredentialId UUID
     * @param binary multipart form-data file upload field
     * @param credentialCreatedOn
     * @return ResponseEntity<AppBinaryDTO>
     * @throws IOException in the event that file operations fail
     */
    @RequestMapping(
            path = "/binary",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AppBinaryDTO> detectAndStoreBinary(@RequestParam UUID storageCredentialId,
                                                             @RequestParam("binary") MultipartFile binary,
                                                             @RequestParam Instant credentialCreatedOn) throws IOException {
//        https://www.baeldung.com/spring-multipartfile-to-file
        File tempFile = File.createTempFile("binary", binary.getOriginalFilename());
        binary.transferTo(tempFile);
        AppBinary appBinary = appBinaryService.detectAndStoreBinary(storageCredentialId, credentialCreatedOn, tempFile);
        tempFile.delete();
        return new ResponseEntity<>(appBinaryMapper.toDTO(appBinary), HttpStatus.CREATED);
    }
}
