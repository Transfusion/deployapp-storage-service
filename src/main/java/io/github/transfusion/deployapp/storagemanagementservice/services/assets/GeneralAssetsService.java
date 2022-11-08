package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.AppInfo;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPAIconHash;
import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryAssetRepository;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.services.AppBinaryJobService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.apache.commons.lang3.NotImplementedException;
import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.assets.Constants.GENERAL_ASSET.PUBLIC_ICON;

@Service
public class GeneralAssetsService {

    @Autowired
    @Qualifier("polyglotContext")
    private Context polyglotCtx;

    @Autowired
    private AppBinaryRepository appBinaryRepository;

    @Autowired
    private AppBinaryAssetRepository appBinaryAssetRepository;

    @Autowired
    private StorageService storageService;

    private AppBinaryAsset generateIPAPublicIcon(Ipa binary) throws IOException {
        UUID appBinaryId = binary.getId();
        File tempFile = storageService.downloadPrivateAppBinaryObject(binary.getStorageCredential(), Instant.now(), appBinaryId, "binary.ipa");

        // now use app-info-graalvm to get our desired asset...
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(tempFile.getAbsolutePath());
        if (!(data instanceof IPA))
            throw new RuntimeException(String.format("The stored AppBinary with ID %s is not actually an IPA", appBinaryId));

        IPAIconHash[] icons = ((IPA) data).icons_(true);
        if (icons == null || icons.length == 0)
            throw new RuntimeException(String.format("The stored AppBinary with ID %s has no icons", appBinaryId));

        // find the largest icon
        Arrays.sort(icons, Comparator.<IPAIconHash>comparingLong(i -> i.dimensions()[0]).reversed());

        String iconName = icons[0].name();
        File iconFile = new File(icons[0].uncrushed_file());
        storageService.uploadPublicAppBinaryObject(binary.getStorageCredential(),
                Instant.now(),
                appBinaryId,
                iconName, iconFile);

        // record in DB

        // delete all existing PUBLIC_ICONs
        appBinaryAssetRepository.deleteByAppBinaryIdAndType(appBinaryId, PUBLIC_ICON.toString());

        AppBinaryAsset asset = new AppBinaryAsset();
        asset.setFileName(iconName);
        asset.setId(UUID.randomUUID());
        asset.setAppBinary(binary);
        asset.setType(PUBLIC_ICON.toString());
        asset.setStatus(Constants.ASSET_STATUS.SUCCESS.toString());
        asset = appBinaryAssetRepository.save(asset); // optimize for reentrancy

        return asset;
    }

    private AppBinaryAsset generatePublicIcon(UUID appBinaryId) throws IOException {
        Optional<AppBinary> _binary = appBinaryRepository.findById(appBinaryId);
        if (_binary.isEmpty()) throw new IllegalArgumentException(String.format("%s doesn't exist", appBinaryId));

        AppBinary binary = _binary.get();
        if (binary instanceof Ipa) {
            return generateIPAPublicIcon((Ipa) binary);
        }

        throw new NotImplementedException(String.format("Generating public app icon of type %s is not implemented", binary.getClass().getName()));
    }

    public AppBinaryAsset generatePublicIcon(UUID jobId, UUID appBinaryId) throws IOException {
        appBinaryJobService.createJob(jobId, appBinaryId, "Generating public icon", "In Progress");
        AppBinaryAsset asset = generatePublicIcon(appBinaryId);
        appBinaryJobService.deleteJobSilent(jobId);
        return asset;
    }

    /**
     * @param id {@link java.util.UUID} of the {@link AppBinaryAsset}
     * @return downloadable {@link URL}
     */
    public URL getURL(UUID id) throws IOException {
        Optional<AppBinaryAsset> _asset = appBinaryAssetRepository.findById(id);
        if (_asset.isEmpty()) throw new ResourceNotFoundException("AppBinaryAsset", "id", id);

        AppBinaryAsset asset = _asset.get();
        UUID storageCredentialId = asset.getAppBinary().getStorageCredential();

        return storageService.getURL(storageCredentialId, Instant.now(), asset.getAppBinary().getId(), asset.getFileName(), asset.isPrivate());

    }

    @Autowired
    private AppBinaryJobService appBinaryJobService;

    /**
     * @param appBinaryId {@link java.util.UUID} of the {@link AppBinary}
     * @return downloadable {@link URL}
     */
    public URL getPublicIcon(UUID appBinaryId) throws IOException {
        Optional<AppBinaryAsset> _asset = appBinaryAssetRepository.findByAppBinaryIdAndType(appBinaryId, "PUBLIC_ICON").stream().findFirst();
        if (_asset.isEmpty()) {
            throw new ResourceNotFoundException("AppBinary", "id", appBinaryId);
        }

        AppBinaryAsset asset = _asset.get();
        UUID storageCredentialId = asset.getAppBinary().getStorageCredential();

        return storageService.getURL(storageCredentialId, Instant.EPOCH, asset.getAppBinary().getId(), asset.getFileName(), asset.isPrivate());
    }

}
