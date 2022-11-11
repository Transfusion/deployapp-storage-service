package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

//import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface IDownloader {
    File downloadPrivateAppBinaryObject(/*S3Credential s3Creds,*/ UUID appBinaryId, String name) throws IOException;
}
