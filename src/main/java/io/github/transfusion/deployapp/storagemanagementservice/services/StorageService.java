package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

/**
 * Interfaces with external storage services
 */
@Service
public class StorageService {

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    private S3Client getS3Client(S3Credential s3Creds) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Creds.getAccessKey(),
                s3Creds.getSecretKey());
        S3ClientBuilder clientBuilder = S3Client.builder();
        if (StringUtils.isEmpty(s3Creds.getAwsRegion())) {
            String endpoint = s3Creds.getServer().replace("https://", "");
            URI uri = URI.create(String.format("https://%s", endpoint));
            clientBuilder.endpointOverride(uri);
        } else {
            clientBuilder.region(Region.of(s3Creds.getAwsRegion()));
        }
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(awsCreds));
        return clientBuilder.build();
    }

    private PutObjectResponse uploadPrivateObject(S3Credential s3Creds, UUID id, String name, File binary) {
        final String key = String.format("%s%s/%s", S3Credential.PRIVATE_PREFIX, id, name);
        S3Client client = getS3Client(s3Creds);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();
        return client.putObject(objectRequest, RequestBody.fromFile(binary));
    }

    public void uploadPrivateObject(UUID storageCredentialId,
                                    Instant credentialCreatedOn,
                                    UUID id, String name, File object) throws JsonProcessingException {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        if (credential instanceof S3Credential) {
            uploadPrivateObject((S3Credential) credential, id, name, object);
        } else {

        }
    }

}
