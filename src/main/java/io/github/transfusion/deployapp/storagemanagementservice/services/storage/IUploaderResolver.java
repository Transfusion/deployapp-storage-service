package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.*;

/**
 * Returns the corresponding {@link IUploader} for a given {@link StorageCredential}
 */
public interface IUploaderResolver {
    IUploader apply(S3Credential credential);

    IUploader apply(FtpCredential credential);

    IUploader apply(MockCredential credential);
}
