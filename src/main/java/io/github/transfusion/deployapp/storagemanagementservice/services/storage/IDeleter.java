package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

//import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;

import java.util.UUID;

public interface IDeleter {
    public void deleteStorageCredential();

    public void deleteAllAppBinaryData(/*S3Credential s3Creds,*/ UUID id);
}
