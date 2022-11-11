package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getS3Client;

public class S3Downloader implements IDownloader {
    private final S3Credential s3Creds;

    public S3Downloader(S3Credential s3Creds) {
        this.s3Creds = s3Creds;
    }


    /**
     * Used by {@link StorageService#downloadPrivateAppBinaryObject(UUID, Instant, UUID, String)} if the credentials are of type S3Credential
     * Streams the object into a temporary file and returns it
     *
     * @param appBinaryId the {@link java.util.UUID} of the {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary} it is associated with
     * @param name        a unique string identifying this file
     * @return a temporary {@link java.io.File}; deletion is left up to the user
     * @throws IOException
     */
    @Override
    public File downloadPrivateAppBinaryObject(UUID appBinaryId, String name) throws IOException {
        final String key = StorageService.getS3PrivateFileKey(appBinaryId, name);
        S3Client client = getS3Client(s3Creds);
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = client.getObject(objectRequest);
        // create a temporary file...
        File tempFile = File.createTempFile("temp", name);
        FileUtils.copyInputStreamToFile(responseInputStream, tempFile);
        return tempFile;
    }
}
