package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.APK;
import io.github.transfusion.app_info_java_graalvm.AppInfo.AppInfo;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.converters.JsonStringListConverter;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.IpaRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterSpecification;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterCriteria;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapper;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapper;
import io.github.transfusion.deployapp.storagemanagementservice.services.assets.GeneralAssetsService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.graalvm.polyglot.Context;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class AppBinaryService {

    Logger logger = LoggerFactory.getLogger(AppBinaryService.class);

    public static Map<String, Class<? extends AppBinary>> IDENTIFIER_TO_CLASS_NAME = new HashMap<>();

    static {
        IDENTIFIER_TO_CLASS_NAME.put(Ipa.IDENTIFIER, Ipa.class);
        IDENTIFIER_TO_CLASS_NAME.put(Apk.IDENTIFIER, Apk.class);
    }

    /* private Context createContext() {
        Context ctx = Context.newBuilder().
                allowAllAccess(true).build();
        ctx.eval("ruby", "Encoding.default_external = 'ISO-8859-1'");
        ctx.eval("ruby", "require 'app-info'");
        return ctx;
    } */

    @Autowired
    @Qualifier("polyglotContext")
    private Context polyglotCtx;

    @Autowired
    private StorageService storageService;

    private AppBinary ensureBinaryAvailable(UUID id) {
        Optional<AppBinary> _binary = appBinaryRepository.findById(id);
        if (_binary.isEmpty()) throw new ResourceNotFoundException("AppBinary", "id", id);
        return _binary.get();
    }

//    @Autowired
//    private StorageCredsUpdateService storageCredsUpdateService;

    @Autowired
    private AppDetailsMapper appDetailsMapper;

    @Autowired
    private IpaRepository ipaRepository;

    private Ipa storeIPA(UUID storageCredentialId, Instant credentialCreatedOn, File binary, IPA ipa) throws JsonProcessingException {
        UUID id = UUID.randomUUID();
//        final String fileName = String.format("%s-%s-%s.ipa", ipa.bundle_id(), ipa.build_version(), ipa.release_version());
        // attempt to upload first
        storageService.uploadPrivateAppBinaryObject(storageCredentialId, credentialCreatedOn, id,
//                String.format("%s-%s-%s.ipa", ipa.bundle_id(), ipa.build_version(), ipa.release_version())
                "binary.ipa", binary);
        // before saving into the db
        Ipa appBinaryRecord = appDetailsMapper.mapPolyglotIPAtoIpa(ipa, id, storageCredentialId, binary.getName());
        return appBinaryRecord;
    }

    /**
     * @param storageCredentialId UUID in storage_credentials
     * @param credentialCreatedOn ISO8601 timestamp
     * @param binary              the file itself
     */
    public AppBinary detectAndStoreOwnBinary(UUID storageCredentialId, Instant credentialCreatedOn, File binary) throws JsonProcessingException {
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(binary.getAbsolutePath());
        if (data instanceof IPA) {
            // TODO: anonymous detect and store
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            UUID userId = ((CustomUserPrincipal) authentication.getPrincipal()).getId();
            Ipa res = storeIPA(storageCredentialId, credentialCreatedOn, binary, (IPA) data);
            res.setUserId(userId);
            ((IPA) data).clear();
            return ipaRepository.save(res);
        } else if (data instanceof APK) {
            return null;
        } else {
            throw new IllegalArgumentException("Only IPA and APK files supported for now.");
        }
    }


    @Autowired
    private AppBinaryRepository appBinaryRepository;

    public Page<AppBinaryDTO> findOwnPaginated(Specification<AppBinary> specification, Pageable pageable) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            // TODO: anonymous listing of uploads
        } else {
            UUID userId = ((CustomUserPrincipal) authentication.getPrincipal()).getId();
            if (specification != null)
                specification = specification.and(new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("userId", "eq", userId)));
            else specification = new AppBinaryFilterSpecification(new AppBinaryFilterCriteria("userId", "eq", userId));

            Page<AppBinary> results = appBinaryRepository.findAll(specification, pageable);
            return results.map(AppBinaryMapper.instance::toDTO);
        }

        return null;
    }

    public AppBinary getAppBinaryById(UUID id) {
//        SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();
//        if (authentication instanceof AnonymousAuthenticationToken) {
//            // TODO: anonymous listing of uploads
//        } else {
        AppBinary binary = ensureBinaryAvailable(id);
        return binary;
//        }
//        return null;
    }

    @Autowired
    private JobScheduler jobScheduler;

    /**
     * Fires off an asynchronous job that deletes all data related to a particular AppBinary.
     *
     * @param id {@link UUID} of the {@link AppBinary}
     */
    public void deleteAppBinaryById(UUID id) throws JsonProcessingException {
        logger.info("deleting app binary by id {}", id);
        AppBinary binary = ensureBinaryAvailable(id);
        UUID storageCredentialId = binary.getStorageCredential();

        // delete everything in our database
        appBinaryRepository.delete(binary); // foreign keys are all ON DELETE CASCADE
        logger.info("deleted app binary by id from our database {}", id);
        jobScheduler.enqueue(() -> storageService.deleteAllAppBinaryData(storageCredentialId, Instant.EPOCH, id));
        logger.info("scheduled storage deletion of app binary by id {}", id);
    }

    public AppBinary setDescription(UUID id, String description) {
        AppBinary binary = ensureBinaryAvailable(id);
        binary.setDescription(description);
        return appBinaryRepository.save(binary);
    }

    public AppBinary setAvailable(UUID id, boolean available) {
        AppBinary binary = ensureBinaryAvailable(id);
        binary.setAvailable(available);
        return appBinaryRepository.save(binary);
    }


    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private GeneralAssetsService generalAssetsService;

    /**
     * Populates manifest-template.txt with values from the AppBinary table.
     *
     * @param id {@link java.util.UUID} of the AppBinary
     * @return the populated manifest.plist
     * @throws IOException
     */
    public String getITMSPlist(UUID id) throws IOException {
        AppBinary binary = ensureBinaryAvailable(id);
        if (!(binary instanceof Ipa)) throw new IllegalArgumentException(String.format("%s is not an IPA.", id));
        if (!binary.getAvailable())
            throw new AccessDeniedException(String.format("AppBinary with id %s is not available", id));
        // load itms plist template from the ResourceLoader
        Resource resource = resourceLoader.getResource("classpath:manifest-template.txt");
        String template = AppBinaryUtils.resourceAsString(resource);

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("downloadableUrl", StringEscapeUtils.escapeXml(storageService.getURL(binary.getStorageCredential(), Instant.EPOCH,
                id, "binary.ipa", true).toString()));

        URL iconURL = generalAssetsService.getPublicIcon(id);
        valuesMap.put("displayImage", StringEscapeUtils.escapeXml(iconURL.toString()));
        valuesMap.put("fullSizeImage", StringEscapeUtils.escapeXml(iconURL.toString()));

        valuesMap.put("bundleId", binary.getIdentifier());
        valuesMap.put("bundleVersion", binary.getVersion());
        valuesMap.put("name", binary.getName());
        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        String g = sub.replace(template);
        System.out.println(g);
        return g;
    }
}
