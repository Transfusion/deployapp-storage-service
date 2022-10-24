package io.github.transfusion.deployapp.storagemanagementservice.services.assets;

import io.github.transfusion.deployapp.exceptions.ResourceNotFoundException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAsset;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryAssetRepository;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class GeneralAssetsService {

    @Autowired
    private AppBinaryAssetRepository repository;

    @Autowired
    private StorageService storageService;

    /**
     * @param id {@link java.util.UUID} of the {@link AppBinaryAsset}
     * @return downloadable {@link URL}
     */
    public URL getURL(UUID id) throws IOException {
        Optional<AppBinaryAsset> _asset = repository.findById(id);
        if (_asset.isEmpty()) throw new ResourceNotFoundException("AppBinaryAsset", "id", id);

        AppBinaryAsset asset = _asset.get();
        UUID storageCredentialId = asset.getAppBinary().getStorageCredential();

        return storageService.getURL(storageCredentialId, Instant.now(), asset.getAppBinary().getId(), asset.getFileName(), asset.isPrivate());

    }

}
