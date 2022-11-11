package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;

/**
 * Returns the corresponding {@link IURLGetter} for a given {@link StorageCredential}
 */
public interface IURLGetterResolver {
    IURLGetter apply(S3Credential credential);

    IURLGetter apply(FtpCredential credential);
}
