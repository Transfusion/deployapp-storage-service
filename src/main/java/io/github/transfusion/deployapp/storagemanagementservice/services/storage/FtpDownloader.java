package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import org.apache.commons.net.ftp.FTPClient;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getFTPClient;
import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.getFtpPrivateFileKey;

public class FtpDownloader implements IDownloader {

    private final FtpCredential ftpCreds;

    public FtpDownloader(FtpCredential ftpCreds) {
        this.ftpCreds = ftpCreds;
    }

    @Override
    public File downloadPrivateAppBinaryObject(UUID appBinaryId, String name) throws IOException, AuthenticationException {
        FTPClient client = getFTPClient(ftpCreds);
        String finalPath = getFtpPrivateFileKey(ftpCreds.getDirectory(), appBinaryId, name);
        File tempFile = File.createTempFile("temp", name);
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        client.retrieveFile(finalPath, outputStream);
        return tempFile;
    }
}
