package io.github.transfusion.deployapp.storagemanagementservice.external_integration;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential.CUSTOM_AWS_REGION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Assume everything has been initialized correctly by the main service
 */
@ExtendWith({SpringExtension.class,})
@Import({UploaderResolver.class, DownloaderResolver.class, DeleterResolver.class, URLGetterResolver.class, RestTemplate.class})
public class S3IntegrationTests {

    @Autowired
    private IUploaderResolver uploaderResolver;

    @Autowired
    private IDownloaderResolver downloaderResolver;

    @Autowired
    private IDeleterResolver deleterResolver;

    public static S3Client getS3Client(S3Credential s3Creds) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Creds.getAccessKey(),
                s3Creds.getSecretKey());
        S3ClientBuilder clientBuilder = S3Client.builder();
        if (s3Creds.getAwsRegion().equals(CUSTOM_AWS_REGION)) {
            String endpoint = s3Creds.getServer().replace("http://", "");
            URI uri = URI.create(String.format("http://%s", endpoint));
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
            String endpoint = s3Creds.getServer().replace("http://", "");
            URI uri = URI.create(String.format("http://%s", endpoint));
            preSignerBuilder.endpointOverride(uri);
            preSignerBuilder.region(Region.US_EAST_1);
        } else {
            preSignerBuilder.region(Region.of(s3Creds.getAwsRegion()));
        }
        preSignerBuilder.credentialsProvider(StaticCredentialsProvider.create(awsCreds));
        return preSignerBuilder.build();
    }

    @Test
    public void privateUploadDownloadDeleteTest() throws Exception {
        UUID appBinaryId = UUID.randomUUID();

        S3Credential creds = new S3Credential();
        creds.setAccessKey("access_key");
        creds.setSecretKey("secret_key");
        creds.setBucket("samplebucket");

        creds.setAwsRegion("custom");
        creds.setServer("minio:9000");

        try (MockedStatic<StorageService> storageServiceMockedStatic =
                     Mockito.mockStatic(StorageService.class, Mockito.CALLS_REAL_METHODS)) {
            storageServiceMockedStatic.when(() -> StorageService.getS3Client(creds))
                    .thenReturn(S3IntegrationTests.getS3Client(creds));

            File tempFile = File.createTempFile("testing", null, null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write("sample123".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

            IUploader s3Uploader = creds.resolveUploader(uploaderResolver);
            s3Uploader.uploadPrivateAppBinaryObject(appBinaryId, "sample", tempFile);

            IDownloader s3Downloader = creds.resolveDownloader(downloaderResolver);
            File downloadedFile = s3Downloader.downloadPrivateAppBinaryObject(appBinaryId, "sample");
            String contents = Files.readString(downloadedFile.toPath());
            assertEquals("sample123", contents);

            tempFile.delete();
            downloadedFile.delete();

            IDeleter deleter = creds.resolveDeleter(deleterResolver);
            deleter.deleteAllAppBinaryData(appBinaryId);

            assertThrows(Exception.class, () -> s3Downloader.downloadPrivateAppBinaryObject(appBinaryId, "sample"));
        }


    }

    @Autowired
    private IURLGetterResolver urlGetterResolver;

    @Test
    public void getURLTest() throws Exception {
        UUID appBinaryId = UUID.randomUUID();

        S3Credential creds = new S3Credential();
        creds.setAccessKey("access_key");
        creds.setSecretKey("secret_key");
        creds.setBucket("samplebucket");

        creds.setAwsRegion("custom");
        creds.setServer("minio:9000");

        try (MockedStatic<StorageService> storageServiceMockedStatic =
                     Mockito.mockStatic(StorageService.class, Mockito.CALLS_REAL_METHODS)) {
            storageServiceMockedStatic.when(() -> StorageService.getS3Client(creds))
                    .thenReturn(S3IntegrationTests.getS3Client(creds));
            storageServiceMockedStatic.when(() -> StorageService.getS3Presigner(creds))
                    .thenReturn(S3IntegrationTests.getS3Presigner(creds));

            File tempFile = File.createTempFile("testing", null, null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write("sample123".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

            IUploader s3Uploader = creds.resolveUploader(uploaderResolver);
            s3Uploader.uploadPrivateAppBinaryObject(appBinaryId, "sample", tempFile);

            IURLGetter urlGetter = creds.resolveURLGetter(urlGetterResolver);
//        there is no distinction between public and private with FTP storage...
            URL url = urlGetter.getPrivateURL(appBinaryId, "sample");

            RestTemplate restTemplate = new RestTemplate();
            String resp = restTemplate.getForObject(url.toURI(), String.class);
            assertEquals("sample123", resp);

            tempFile = File.createTempFile("testing", null, null);
            fos = new FileOutputStream(tempFile);
            fos.write("123sample".getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

            s3Uploader.uploadPublicAppBinaryObject(appBinaryId, "sample2", tempFile);
            url = urlGetter.getPublicURL(appBinaryId, "sample2");
            resp = restTemplate.getForObject(url.toURI(), String.class);
            assertEquals("123sample", resp);
        }
    }
}
