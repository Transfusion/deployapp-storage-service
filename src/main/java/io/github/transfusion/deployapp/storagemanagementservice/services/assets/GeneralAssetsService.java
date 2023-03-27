package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import io.github.transfusion.app_info_java_graalvm.AbstractPolyglotAdapter;
import io.github.transfusion.app_info_java_graalvm.AppInfo.*;
import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
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
import java.util.*;

import static io.github.transfusion.deployapp.storagemanagementservice.services.assets.Constants.GENERAL_ASSET.PUBLIC_ICON;
import static io.github.transfusion.deployapp.utilities.GraalPolyglot.cleanUpRubyContext;

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

    private AppBinaryAsset generateIPAPublicIcon(Ipa binary) throws Exception {
        UUID appBinaryId = binary.getId();
        File tempFile = storageService.downloadPrivateAppBinaryObject(binary.getStorageCredential(), Instant.now(), appBinaryId, "binary.ipa");

        // now use app-info-graalvm to get our desired asset...
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(tempFile.getAbsolutePath());
        if (!(data instanceof IPA))
            throw new RuntimeException(String.format("The stored AppBinary with ID %s is not actually an IPA", appBinaryId));

        List<IPAIconHash> icons = ((IPA) data).icons_(true);
        if (icons == null || icons.size() == 0)
            throw new RuntimeException(String.format("The stored AppBinary with ID %s has no icons", appBinaryId));

        // find the largest icon
        icons.sort(Comparator.<IPAIconHash>comparingLong(i -> i.dimensions()[0]).reversed());

        String iconName = icons.get(0).name();
        // noticed .uncrushed_file() being null and .file() being already uncrushed in the wild
        File iconFile = new File(icons.get(0).uncrushed_file() != null ? icons.get(0).uncrushed_file() : icons.get(0).file());
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

        ((IPA) data).clear();
        cleanUpRubyContext(polyglotCtx);

        return asset;
    }

    private AppBinaryAsset generateAPKPublicIcon(Apk binary) throws Exception {
        UUID appBinaryId = binary.getId();
        File tempFile = storageService.downloadPrivateAppBinaryObject(binary.getStorageCredential(), Instant.now(), appBinaryId, "binary.apk");

        // now use app-info-graalvm to get our desired asset...
        AppInfo appInfo = AppInfo.getInstance(polyglotCtx);
        AbstractPolyglotAdapter data = appInfo.parse_(tempFile.getAbsolutePath());
        if (!(data instanceof APK))
            throw new RuntimeException(String.format("The stored AppBinary with ID %s is not actually an APK", appBinaryId));

        List<AndroidIconHash> icons = ((APK) data).icons_();
        if (icons == null || icons.size() == 0)
            throw new RuntimeException(String.format("The stored AppBinary with ID %s has no icons", appBinaryId));

        // find the largest icon ( AndroidIconHash.dimensions() sometimes returns null in the wild )
        icons.sort(Comparator.<AndroidIconHash>comparingLong(i -> i.dimensions() == null ? 0 : i.dimensions()[0]).reversed());

        String iconName = icons.get(0).name();
        // noticed .uncrushed_file() being null and .file() being already uncrushed in the wild
        File iconFile = new File(icons.get(0).file());
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

        ((APK) data).clear();
        cleanUpRubyContext(polyglotCtx);

        return asset;
    }

    private AppBinaryAsset generatePublicIcon(UUID appBinaryId) throws Exception {
        Optional<AppBinary> _binary = appBinaryRepository.findById(appBinaryId);
        if (_binary.isEmpty()) throw new IllegalArgumentException(String.format("%s doesn't exist", appBinaryId));

        AppBinary binary = _binary.get();
        if (binary instanceof Ipa) {
            return generateIPAPublicIcon((Ipa) binary);
        } else if (binary instanceof Apk) {
            return generateAPKPublicIcon((Apk) binary);
        }

        throw new NotImplementedException(String.format("Generating public app icon of type %s is not implemented", binary.getClass().getName()));
    }

    public AppBinaryAsset generatePublicIcon(UUID jobId, UUID appBinaryId) throws Exception {
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
