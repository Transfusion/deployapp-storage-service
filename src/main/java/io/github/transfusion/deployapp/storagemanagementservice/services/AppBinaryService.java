package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.APK;
import io.github.transfusion.app_info_java_graalvm.AppInfo.AppInfo;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import io.github.transfusion.deployapp.dto.response.AppBinaryDTO;
import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.IpaRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterSpecification;
import io.github.transfusion.deployapp.storagemanagementservice.db.specifications.AppBinaryFilterCriteria;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryMapper;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapper;
import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class AppBinaryService {

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
}
