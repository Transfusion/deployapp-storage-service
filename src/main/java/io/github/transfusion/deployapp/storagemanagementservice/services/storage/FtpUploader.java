package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential.PUBLIC_PREFIX;
import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential.PRIVATE_PREFIX;
import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.*;

public class FtpUploader implements IUploader {

    private final FtpCredential ftpCreds;

    public FtpUploader(FtpCredential ftpCreds) {
        this.ftpCreds = ftpCreds;
    }

    @Override
    public void uploadPublicAppBinaryObject(UUID appBinaryId, String name, File binary) throws AuthenticationException, IOException {
        FTPClient client = getFTPClient(ftpCreds);
        client.makeDirectory(getFtpPublicAppBinaryDirectory(ftpCreds.getDirectory(), appBinaryId));
        String finalPath = getFtpPublicFileKey(ftpCreds.getDirectory(), appBinaryId, name);
        client.storeFile(finalPath, new FileInputStream(binary));
    }

    // there is no real concept of private with FTP...
    @Override
    public void uploadPrivateAppBinaryObject(UUID appBinaryId, String name, File binary) throws AuthenticationException, IOException {
        FTPClient client = getFTPClient(ftpCreds);
        client.makeDirectory(getFtpPrivateAppBinaryDirectory(ftpCreds.getDirectory(), appBinaryId));
        String finalPath = getFtpPrivateFileKey(ftpCreds.getDirectory(), appBinaryId, name);
        client.storeFile(finalPath, new FileInputStream(binary));
    }
}
