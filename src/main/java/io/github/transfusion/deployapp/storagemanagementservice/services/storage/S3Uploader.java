package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getS3Client;

public class S3Uploader implements IUploader {
    private final S3Credential s3Creds;

    public S3Uploader(S3Credential s3Creds) {
        this.s3Creds = s3Creds;
    }

    @Override
    public void uploadPublicAppBinaryObject(UUID appBinaryId, String name, File binary) {
        final String key = StorageService.getS3PublicFileKey(appBinaryId, name);
        S3Client client = getS3Client(s3Creds);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();
        /*return*/
        client.putObject(objectRequest, RequestBody.fromFile(binary));
    }

    @Override
    public void uploadPrivateAppBinaryObject(UUID appBinaryId, String name, File binary) {
        final String key = StorageService.getS3PrivateFileKey(appBinaryId, name);
        S3Client client = getS3Client(s3Creds);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();
        /*return*/
        client.putObject(objectRequest, RequestBody.fromFile(binary));
    }

    @Override
    public void abort() {
        // no-op!
    }
}
