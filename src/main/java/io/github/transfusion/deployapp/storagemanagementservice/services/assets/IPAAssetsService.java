package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.AppInfo;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.storagemanagementservice.controller.AppController;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.IpaMobileprovision;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryAssetRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.IpaMobileprovisionRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.MobileProvisionMapper;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.graalvm.polyglot.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.assets.Constants.IPA_ASSETS.MOBILEPROVISION;

@Service
public class IPAAssetsService {

    Logger logger = LoggerFactory.getLogger(IPAAssetsService.class);

    @Autowired
    private AppBinaryRepository appBinaryRepository;

    @Autowired
    private AppBinaryAssetRepository appBinaryAssetRepository;

//    @Autowired
//    private AppBinaryService appBinaryService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    @Autowired
    @Qualifier("polyglotContext")
    private Context polyglotCtx;


    @Autowired
    private MobileProvisionMapper mobileProvisionMapper;

    @Autowired
    private IpaMobileprovisionRepository ipaMobileprovisionRepository;

    /**
     * @param appBinaryId the {@link java.util.UUID} of an {@link Ipa}
     */
    // TODO: during testing, mock appBinaryRepository, the StorageService that returns a file, and catch the exceptions
    @Transactional
    public AppBinaryAsset generateIPAMobileProvision(UUID appBinaryId) throws IOException {
        Optional<AppBinary> _binary = appBinaryRepository.findById(appBinaryId);
        if (_binary.isEmpty()) throw new IllegalArgumentException(String.format("%s doesn't exist", appBinaryId));
        AppBinary binary = _binary.get();
        if (!(binary instanceof Ipa))
            throw new IllegalArgumentException(String.format("%s is not an IPA.", appBinaryId));

        // once we have the temp file, we have its path on disk also
        File tempFile = storageService.downloadPrivateAppBinaryObject(binary.getStorageCredential(), Instant.now(), appBinaryId, "binary.ipa");

        // now use app-info-graalvm to get our desired asset...
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(tempFile.getAbsolutePath());
        if (!(data instanceof IPA))
            throw new RuntimeException(String.format("The stored AppBinary with ID %s is not actually an IPA", appBinaryId));

        File mobileProvisionFile = new File(((IPA) data).mobileprovision_path());
        // upload back to the storage
        storageService.uploadPrivateAppBinaryObject(binary.getStorageCredential(),
                Instant.now(),
                appBinaryId,
                mobileProvisionFile.getName(), mobileProvisionFile);

        // record in DB
        AppBinaryAsset asset = new AppBinaryAsset();
        asset.setFileName(mobileProvisionFile.getName());
        asset.setId(UUID.randomUUID());
        asset.setAppBinary(binary);
        asset.setType(MOBILEPROVISION.toString());
        asset = appBinaryAssetRepository.save(asset); // optimize for reentrancy

        // and the actual mobileprovision info too for preview purposes...
        IpaMobileprovision mobileProvision = mobileProvisionMapper.mapPolyglotMobileProvisionToIpaMobileProvision(((IPA) data).mobileprovision());
        mobileProvision.setId(UUID.randomUUID());
        mobileProvision.setIpa((Ipa) binary);
        ipaMobileprovisionRepository.save(mobileProvision);

        logger.info("mobileprovision gen done " + binary.getId());

        return asset;
    }
}
