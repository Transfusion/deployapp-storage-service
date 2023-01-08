package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.MockCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;

/**
 * Returns the corresponding {@link IDownloader} for a given {@link StorageCredential}
 */
public interface IDownloaderResolver {
    IDownloader apply(S3Credential s3Credential);

    IDownloader apply(FtpCredential ftpCredential);

    IDownloader apply(MockCredential mockCredential);
}
