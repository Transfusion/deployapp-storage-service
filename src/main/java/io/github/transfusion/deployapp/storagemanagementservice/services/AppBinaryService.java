package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.APK;
import io.github.transfusion.app_info_java_graalvm.AppInfo.AppInfo;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.IpaRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppDetailsMapper;
import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.UUID;


@Service
public class AppBinaryService {

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

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    @Autowired
    private AppDetailsMapper appDetailsMapper;

    @Autowired
    private IpaRepository ipaRepository;

    private Ipa storeIPA(UUID storageCredentialId, Instant credentialCreatedOn, File binary, IPA ipa) throws JsonProcessingException {
        UUID id = UUID.randomUUID();
//        final String fileName = String.format("%s-%s-%s.ipa", ipa.bundle_id(), ipa.build_version(), ipa.release_version());
        // attempt to upload first
        storageService.uploadPrivateObject(storageCredentialId, credentialCreatedOn, id,
//                String.format("%s-%s-%s.ipa", ipa.bundle_id(), ipa.build_version(), ipa.release_version())
                "binary.ipa"
                , binary);
        // before saving into the db
        Ipa appBinaryRecord = appDetailsMapper.mapPolyglotIPAtoIpa(ipa, id, storageCredentialId, binary.getName());

        return ipaRepository.save(appBinaryRecord);
    }

    /**
     * @param storageCredentialId UUID in storage_credentials
     * @param credentialCreatedOn ISO8601 timestamp
     * @param binary the
     */
    public AppBinary detectAndStoreBinary(UUID storageCredentialId, Instant credentialCreatedOn, File binary) throws JsonProcessingException {
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(binary.getAbsolutePath());
        if (data instanceof IPA) {
            Ipa res = storeIPA(storageCredentialId, credentialCreatedOn, binary, (IPA) data);
            ((IPA) data).clear();
            return res;
        } else if (data instanceof APK) {
            return null;
        } else {
            throw new IllegalArgumentException("Only IPA and APK files supported for now.");
        }
    }

}
