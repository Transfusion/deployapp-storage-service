package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getS3Client;
import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getS3Presigner;

public class S3URLGetter implements IURLGetter {

    private final S3Credential s3Creds;

    public S3URLGetter(S3Credential s3Creds) {
        this.s3Creds = s3Creds;
    }

    @Override
    public URL getPrivateURL(UUID appBinaryId, String name) {
        final String key = StorageService.getS3PrivateFileKey(appBinaryId, name);

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(objectRequest).build();

        S3Presigner presigner = getS3Presigner(s3Creds);
        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(request);
        URL result = presignedGetObjectRequest.url();
        presigner.close();
        return result;
    }

    @Override
    public URL getPublicURL(UUID appBinaryId, String name) {
        S3Client client = getS3Client(s3Creds);
        final String key = StorageService.getS3PublicFileKey(appBinaryId, name);
        return client.utilities().getUrl(bldr -> bldr.bucket(s3Creds.getBucket()).key(key));
    }
}
