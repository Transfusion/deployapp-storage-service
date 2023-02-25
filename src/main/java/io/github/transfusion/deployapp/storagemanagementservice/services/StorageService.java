package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryRepository;
import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.CustomFTPClient;
import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential.PRIVATE_PREFIX;
import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential.PUBLIC_PREFIX;
import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential.CUSTOM_AWS_REGION;

/**
 * Interfaces with external storage services
 */
@Service
public class StorageService {

    Logger logger = LoggerFactory.getLogger(StorageService.class);

    public static String getS3PrivateFileKey(UUID appBinaryId, String name) {
        return String.format("%s%s/%s", S3Credential.PRIVATE_PREFIX, appBinaryId, name);
    }

    public static String getS3PublicFileKey(UUID appBinaryId, String name) {
        return String.format("%s%s/%s", S3Credential.PUBLIC_PREFIX, appBinaryId, name);
    }

    public static String getFtpPrivatePrefixDirectory(String directory) {
        return String.format("%s/%s", directory, PRIVATE_PREFIX);
    }

    public static String getFtpPrivateAppBinaryDirectory(String directory, UUID appBinaryId) {
        return String.format("%s/%s%s", directory, PRIVATE_PREFIX, appBinaryId);
    }

    public static String getFtpPrivateFileKey(String directory, UUID appBinaryId, String name) {
        return String.format("%s/%s%s/%s", directory, PRIVATE_PREFIX, appBinaryId, name);
    }

    public static String getFtpPublicPrefixDirectory(String directory) {
        return String.format("%s/%s", directory, PUBLIC_PREFIX);
    }

    public static String getFtpPublicAppBinaryDirectory(String directory, UUID appBinaryId) {
        return String.format("%s/%s%s", directory, PUBLIC_PREFIX, appBinaryId);
    }

    public static String getFtpPublicFileKey(String directory, UUID appBinaryId, String name) {
        return String.format("%s/%s%s/%s", directory, PUBLIC_PREFIX, appBinaryId, name);
    }

    public static CustomFTPClient getFTPClient(FtpCredential ftpCreds) throws IOException, AuthenticationException {
        CustomFTPClient client = new CustomFTPClient();
        client.setConnectTimeout(60000);
        client.connect(ftpCreds.getServer(), ftpCreds.getPort());
        // https://stackoverflow.com/questions/29848713/how-to-use-socket-setsotimeout
        client.setSoTimeout(60000);
        client.setDataTimeout(60000);
        // https://stackoverflow.com/questions/21294253/commons-ftpclient-storefile-hangs-if-ftp-server-becomes-unavailable
        client.setControlKeepAliveTimeout(60); // 60 SECONDS

        if (!client.login(ftpCreds.getUsername(), ftpCreds.getPassword()))
            throw new AuthenticationException(String.format("Login failed to server %s port %d", ftpCreds.getServer(), ftpCreds.getPort()));
        client.makeDirectory(getFtpPublicPrefixDirectory(ftpCreds.getDirectory()));
        client.makeDirectory(getFtpPrivatePrefixDirectory(ftpCreds.getDirectory()));
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode(); // default to PASV mode
        return client;
    }


    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    @Autowired
    private IUploaderResolver uploaderResolver;

    @Autowired
    private IDownloaderResolver downloaderResolver;

    @Autowired
    private IURLGetterResolver urlGetterResolver;

    @Autowired
    private IDeleterResolver deleterResolver;

    public static S3Client getS3Client(S3Credential s3Creds) {
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

    public static S3Presigner getS3Presigner(S3Credential s3Creds) {
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
                                             UUID id, String name, File object) throws Exception {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        credential.resolveUploader(uploaderResolver).uploadPrivateAppBinaryObject(id, name, object);
//            TODO: fill in other credential methods
    }

    /**
     * Used in {@link io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService} to be able to call {@link IUploader#abort()} from another thread
     *
     * @param storageCredentialId the {@link java.util.UUID} of the given {@link StorageCredential}
     * @param credentialCreatedOn the creation {@link java.time.Instant} of the given {@link StorageCredential} at the time of reading
     * @return the {@link IUploader} implementation
     * @throws JsonProcessingException thrown if the main microservice is down
     */
    public IUploader resolveUploader(UUID storageCredentialId,
                                     Instant credentialCreatedOn) throws JsonProcessingException {
        return storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn).resolveUploader(uploaderResolver);
    }

    /**
     * Downloads a file from private storage; for processing uses
     *
     * @param storageCredentialId the {@link java.util.UUID} of the given {@link StorageCredential}
     * @param appBinaryId         the {@link java.util.UUID} of the {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary} it is associated with
     * @param name                a unique string identifying this file
     * @return a temporary {@link java.io.File}; deletion is left up to the user
     */
    public File downloadPrivateAppBinaryObject(UUID storageCredentialId, Instant credentialCreatedOn, UUID appBinaryId, String name) throws Exception {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        return credential.resolveDownloader(downloaderResolver).downloadPrivateAppBinaryObject(appBinaryId, name);
//            TODO: fill in other credential methods
    }

    /* AppBinaryAsset-related methods go below */

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
        if (_private) return credential.resolveURLGetter(urlGetterResolver).getPrivateURL(appBinaryId, name);
        else return credential.resolveURLGetter(urlGetterResolver).getPublicURL(appBinaryId, name);
//            TODO: fill in other credential methods
    }

    public void uploadPublicAppBinaryObject(UUID storageCredentialId,
                                            Instant credentialCreatedOn,
                                            UUID id, String name, File object) throws Exception {
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        credential.resolveUploader(uploaderResolver).uploadPublicAppBinaryObject(id, name, object);
//            TODO: fill in other credential methods
    }

    /* deletion / cleanup methods go below here */

    public void deleteAllAppBinaryData(UUID storageCredentialId, Instant credentialCreatedOn, UUID id) throws Exception {
        logger.info("storageservice delete started with credential id {}", storageCredentialId);
        StorageCredential credential = storageCredsUpdateService.getCredential(storageCredentialId, credentialCreatedOn);
        credential.resolveDeleter(deleterResolver).deleteAllAppBinaryData(id);
//            TODO: fill in other credential methods
        logger.info("storageservice delete ended with credential id {}", storageCredentialId);
    }

    @Autowired
    private AppBinaryRepository appBinaryRepository;

    @Autowired
    private JobScheduler jobScheduler;

    public void deleteStorageCredential(StorageCredential credential) throws Exception {
        credential.resolveDeleter(deleterResolver).deleteStorageCredential();
        storageCredsUpdateService.deleteCredential(credential.getId());
    }

    /**
     * Nukes everything DeployApp-related stored using the given credential.
     *
     * @param storageCredentialId {@link UUID} of the {@link StorageCredential}
     */
    public void deleteStorageCredential(UUID storageCredentialId) {
        logger.info("starting nuke of storage credential {}", storageCredentialId);
        appBinaryRepository.deleteByStorageCredential(storageCredentialId);
        StorageCredential credential;
        try {
            credential = storageCredsUpdateService.getCredential(storageCredentialId, Instant.EPOCH);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("storage credential with id {} is already gone at this point", storageCredentialId);
            return;
        }

        logger.info("storage credential {} successfully obtained", storageCredentialId);
        jobScheduler.enqueue(() -> deleteStorageCredential(credential));
//            TODO: fill in other credential methods
    }
}
