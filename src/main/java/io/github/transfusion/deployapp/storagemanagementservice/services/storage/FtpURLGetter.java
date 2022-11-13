package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential.PUBLIC_PREFIX;
import static io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential.PRIVATE_PREFIX;

public class FtpURLGetter implements IURLGetter {

    private final FtpCredential ftpCreds;

    public FtpURLGetter(FtpCredential ftpCreds) {
        this.ftpCreds = ftpCreds;
    }

    @Override
    public URL getPrivateURL(UUID appBinaryId, String name) throws MalformedURLException {
        return new URL(ftpCreds.getBaseUrl() + '/' + PRIVATE_PREFIX + '/' + appBinaryId + '/' + name);
    }

    @Override
    public URL getPublicURL(UUID appBinaryId, String name) throws MalformedURLException {
        return new URL(ftpCreds.getBaseUrl() + '/' + PUBLIC_PREFIX + '/' + appBinaryId + '/' + name);
    }
}
