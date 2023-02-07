package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.CustomFTPClient;
//import org.apache.commons.net.ftp.FTPClient;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.*;

public class FtpUploader implements IUploader {

    private final FtpCredential ftpCreds;

    private CustomFTPClient client;

    public FtpUploader(FtpCredential ftpCreds) {
        this.ftpCreds = ftpCreds;
    }

    @Override
    public void uploadPublicAppBinaryObject(UUID appBinaryId, String name, File binary) throws AuthenticationException, IOException {
        client = getFTPClient(ftpCreds);
        client.makeDirectory(getFtpPublicAppBinaryDirectory(ftpCreds.getDirectory(), appBinaryId));
        String finalPath = getFtpPublicFileKey(ftpCreds.getDirectory(), appBinaryId, name);

        client.enterLocalPassiveMode();
        boolean successful = client.storeFile(finalPath, new FileInputStream(binary));
        if (!successful) {
            client.enterLocalActiveMode();
            successful = client.storeFile(finalPath, new FileInputStream(binary));
        }
        client.enterLocalPassiveMode(); // RESET!

        try {
            client.disconnect();
        } catch (Exception caught) {
            caught.printStackTrace();
        }
    }

    // there is no real concept of private with FTP...
    @Override
    public void uploadPrivateAppBinaryObject(UUID appBinaryId, String name, File binary) throws AuthenticationException, IOException {
        client = getFTPClient(ftpCreds);
        client.makeDirectory(getFtpPrivateAppBinaryDirectory(ftpCreds.getDirectory(), appBinaryId));
        String finalPath = getFtpPrivateFileKey(ftpCreds.getDirectory(), appBinaryId, name);
        client.enterLocalPassiveMode();
        boolean successful = client.storeFile(finalPath, new FileInputStream(binary));
        if (!successful) {
            client.enterLocalActiveMode();
            client.storeFile(finalPath, new FileInputStream(binary));
        }
        try {
            client.disconnect();
        } catch (Exception caught) {
            caught.printStackTrace();
        }
    }

    @Override
    public void abort() {
        try {
            client.abort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
