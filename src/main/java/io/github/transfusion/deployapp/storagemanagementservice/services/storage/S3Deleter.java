package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getS3Client;

public class S3Deleter implements IDeleter {
    Logger logger = LoggerFactory.getLogger(S3Deleter.class);

    private final S3Credential s3Creds;

    public S3Deleter(S3Credential s3Creds) {
        this.s3Creds = s3Creds;
    }


    private void performDelete(S3Client s3Client, S3Credential s3Creds, ListObjectsResponse response) {
        while (true) {
            if (response.contents() == null) {
                break;
            }
            for (S3Object objectSummary : response.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder().bucket(s3Creds.getBucket()).key(objectSummary.key()).build());
            }
            if (response.isTruncated()) {
                response = s3Client.listObjects(ListObjectsRequest.builder().marker(response.nextMarker()).build());
            } else {
                break;
            }
        }
    }

    @Override
    public void deleteStorageCredential() {
        logger.info("nuking s3 storage credential with id {}", s3Creds.getId());
        S3Client s3Client = getS3Client(s3Creds);

        // delete private prefix first
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(s3Creds.getBucket())
                .prefix(S3Credential.PRIVATE_PREFIX).build();

        ListObjectsResponse response = s3Client.listObjects(listObjectsRequest);
        performDelete(s3Client, s3Creds, response);

        // then public prefix
        listObjectsRequest = ListObjectsRequest.builder()
                .bucket(s3Creds.getBucket())
                .prefix(S3Credential.PUBLIC_PREFIX).build();

        response = s3Client.listObjects(listObjectsRequest);
        performDelete(s3Client, s3Creds, response);

//        storageCredsUpdateService.deleteCredential(s3Creds.getId());
    }

    @Override
    public void deleteAllAppBinaryData(UUID id) {
        S3Client s3Client = getS3Client(s3Creds);

        // delete private prefix first
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(s3Creds.getBucket())
                .prefix(String.format("%s%s/", S3Credential.PRIVATE_PREFIX, id)).build();

        ListObjectsResponse response = s3Client.listObjects(listObjectsRequest);
        performDelete(s3Client, s3Creds, response);

        // then public prefix
        listObjectsRequest = ListObjectsRequest.builder()
                .bucket(s3Creds.getBucket())
                .prefix(String.format("%s%s/", S3Credential.PUBLIC_PREFIX, id)).build();

        response = s3Client.listObjects(listObjectsRequest);
        performDelete(s3Client, s3Creds, response);
    }
}
