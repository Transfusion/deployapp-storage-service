package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;

public interface IDeleterResolver {
    IDeleter apply(S3Credential s3Credential);

    IDeleter apply(FtpCredential ftpCredential);
}
