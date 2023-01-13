package io.github.transfusion.deployapp.storagemanagementservice.external_integration;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({SpringExtension.class,})
@Import({UploaderResolver.class, DownloaderResolver.class, DeleterResolver.class})
public class FtpIntegrationTests {

    @Autowired
    private IUploaderResolver uploaderResolver;

    @Autowired
    private IDownloaderResolver downloaderResolver;

    @Autowired
    private IDeleterResolver deleterResolver;

    @Test
    public void privateUploadDownloadDeleteTest() throws Exception {
        UUID appBinaryId = UUID.randomUUID();

        FtpCredential creds = new FtpCredential();
        creds.setServer("ftp");
        creds.setPort(21);
        creds.setUsername("test");
        creds.setPassword("test");
        creds.setBaseUrl("http://ftp-web/");
        creds.setDirectory("/");

        File tempFile = File.createTempFile("testing", null, null);
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write("sample123".getBytes(StandardCharsets.UTF_8));
        fos.flush();
        fos.close();

        IUploader ftpUploader = creds.resolveUploader(uploaderResolver);
        ftpUploader.uploadPrivateAppBinaryObject(appBinaryId, "sample", tempFile);

        IDownloader ftpDownloader = creds.resolveDownloader(downloaderResolver);
        File downloadedFile = ftpDownloader.downloadPrivateAppBinaryObject(appBinaryId, "sample");
        String contents = Files.readString(downloadedFile.toPath());
        assertEquals("sample123", contents);

        tempFile.delete();
        downloadedFile.delete();

        IDeleter deleter = creds.resolveDeleter(deleterResolver);
        deleter.deleteAllAppBinaryData(appBinaryId);

        assertThrows(IOException.class, () -> ftpDownloader.downloadPrivateAppBinaryObject(appBinaryId, "sample"));
        // assert is empty?
    }

}
