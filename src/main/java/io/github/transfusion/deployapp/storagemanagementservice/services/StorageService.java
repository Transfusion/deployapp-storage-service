package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential.CUSTOM_AWS_REGION;

/**
 * Interfaces with external storage services
 */
@Service
public class StorageService {

    static String getS3PrivateFileKey(UUID appBinaryId, String name) {
        return String.format("%s%s/%s", S3Credential.PRIVATE_PREFIX, appBinaryId, name);
    }

    static String getS3PublicFileKey(UUID appBinaryId, String name) {
        return String.format("%s%s/%s", S3Credential.PUBLIC_PREFIX, appBinaryId, name);
    }

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    private S3Client getS3Client(S3Credential s3Creds) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Creds.getAccessKey(),
                s3Creds.getSecretKey());
        S3ClientBuilder clientBuilder = S3Client.builder();
        if (s3Creds.getAwsRegion().equals(CUSTOM_AWS_REGION)) {
            String endpoint = s3Creds.getServer().replace("https://", "");
            URI uri = URI.create(String.format("https://%s", endpoint));
            clientBuilder.endpointOverride(uri);
            clientBuilder.region(Region.US_EAST_1); // the default
        } else {
            clientBuilder.region(Region.of(s3Creds.getAwsRegion()));
        }
        clientBuilder.credentialsProvider(StaticCredentialsProvider.create(awsCreds));
        return clientBuilder.build();
    }

    private S3Presigner getS3Presigner(S3Credential s3Creds) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Creds.getAccessKey(),
                s3Creds.getSecretKey());
        S3Presigner.Builder preSignerBuilder = S3Presigner.builder();
        if (s3Creds.getAwsRegion().equals(CUSTOM_AWS_REGION)) {
            String endpoint = s3Creds.getServer().replace("https://", "");
            URI uri = URI.create(String.format("https://%s", endpoint));
            preSignerBuilder.endpointOverride(uri);
            preSignerBuilder.region(Region.US_EAST_1);
        } else {
            preSignerBuilder.region(Region.of(s3Creds.getAwsRegion()));
        }
        preSignerBuilder.credentialsProvider(StaticCredentialsProvider.create(awsCreds));
        return preSignerBuilder.build();
    }

    private PutObjectResponse uploadPrivateAppBinaryObject(S3Credential s3Creds, UUID appBinaryId, String name, File binary) {
        final String key = StorageService.getS3PrivateFileKey(appBinaryId, name);
        S3Client client = getS3Client(s3Creds);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();
        return client.putObject(objectRequest, RequestBody.fromFile(binary));
    }

    /**
     * Uploads a file associated with an AppBinary into private storage
     *
     * @param storageCredentialId the {@link java.util.UUID} of the given {@link StorageCredential}
     * @param credentialCreatedOn the creation {@link java.time.Instant} of the given {@link StorageCredential} at the time of reading
     * @param id                  the app binary ID
     * @param name                a unique string identifying this file
     * @param object              the {@link java.io.File} to be uploaded
     * @throws JsonProcessingException thrown if the main microservice is down
     */
    public void uploadPrivateAppBinaryObject(UUID storageCredentialId,
                                             Instant credentialCreatedOn,
                                             UUID id, String name, File object) throws JsonProcessingException {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        if (credential instanceof S3Credential) {
            uploadPrivateAppBinaryObject((S3Credential) credential, id, name, object);
        } else {
            throw new NotImplementedException(String.format("%s is of unknown storage credential type", storageCredentialId));
//            TODO: fill in other credential methods
        }
    }

    /**
     * Used by {@link #downloadPrivateAppBinaryObject(UUID, Instant, UUID, String)} if the credentials are of type S3Credential
     * Streams the object into a temporary file and returns it
     *
     * @param s3Creds     {@link S3Credential}
     * @param appBinaryId the {@link java.util.UUID} of the {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary} it is associated with
     * @param name        a unique string identifying this file
     * @return a temporary {@link java.io.File}; deletion is left up to the user
     * @throws IOException
     */
    private File downloadPrivateAppBinaryObject(S3Credential s3Creds, UUID appBinaryId, String name) throws IOException {
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

    /**
     * Downloads a file from private storage; for processing uses
     *
     * @param storageCredentialId the {@link java.util.UUID} of the given {@link StorageCredential}
     * @param appBinaryId         the {@link java.util.UUID} of the {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary} it is associated with
     * @param name                a unique string identifying this file
     * @return a temporary {@link java.io.File}; deletion is left up to the user
     */
    public File downloadPrivateAppBinaryObject(UUID storageCredentialId, Instant credentialCreatedOn, UUID appBinaryId, String name) throws IOException {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        if (credential instanceof S3Credential) {
            return downloadPrivateAppBinaryObject((S3Credential) credential, appBinaryId, name);
        } else {
            throw new NotImplementedException(String.format("%s is of unknown storage credential type", storageCredentialId));
//            TODO: fill in other credential methods
        }
    }

    /* AppBinaryAsset-related methods go below */

    /**
     * S3-specific version of {@link #getURL(UUID, Instant, UUID, String, boolean)}
     *
     * @param s3Creds
     * @param appBinaryId
     * @param name
     * @param _private
     * @return
     */
    public URL getURL(S3Credential s3Creds, UUID appBinaryId, String name, boolean _private) {
        S3Client client = getS3Client(s3Creds);
        final String key = _private ? StorageService.getS3PrivateFileKey(appBinaryId, name) :
                StorageService.getS3PublicFileKey(appBinaryId, name);
        if (_private) {
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
        } else {
            return client.utilities().getUrl(bldr -> bldr.bucket(s3Creds.getBucket()).key(key));
        }
    }

    /**
     * @param storageCredentialId the {@link java.util.UUID} of the given {@link StorageCredential}
     * @param appBinaryId         the {@link java.util.UUID} of the {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary} it is associated with
     * @param name                a unique string identifying this file
     * @param _private            Whether the asset in question is private
     * @return downloadable {@link URL}
     * @throws IOException
     */
    public URL getURL(UUID storageCredentialId, Instant credentialCreatedOn, UUID appBinaryId, String name, boolean _private) throws IOException {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);

        if (credential instanceof S3Credential) return getURL((S3Credential) credential, appBinaryId, name, _private);
        throw new NotImplementedException(String.format("%s is of unknown storage credential type", storageCredentialId));
//            TODO: fill in other credential methods
    }

    private PutObjectResponse uploadPublicAppBinaryObject(S3Credential s3Creds, UUID appBinaryId, String name, File binary) {
        final String key = StorageService.getS3PublicFileKey(appBinaryId, name);
        S3Client client = getS3Client(s3Creds);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Creds.getBucket())
                .key(key)
                .build();
        return client.putObject(objectRequest, RequestBody.fromFile(binary));
    }

    public void uploadPublicAppBinaryObject(UUID storageCredentialId,
                                            Instant credentialCreatedOn,
                                            UUID id, String name, File object) throws JsonProcessingException {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        if (credential instanceof S3Credential) {
            uploadPublicAppBinaryObject((S3Credential) credential, id, name, object);
        } else {
            throw new NotImplementedException(String.format("%s is of unknown storage credential type", storageCredentialId));
//            TODO: fill in other credential methods
        }
    }

}
