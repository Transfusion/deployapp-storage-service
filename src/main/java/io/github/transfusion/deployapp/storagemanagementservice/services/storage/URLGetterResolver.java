package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.MockCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import org.springframework.stereotype.Service;

@Service
public class URLGetterResolver implements IURLGetterResolver {

    @Override
    public IURLGetter apply(S3Credential credential) {
        return new S3URLGetter(credential);
    }

    @Override
    public IURLGetter apply(FtpCredential credential) {
        return new FtpURLGetter(credential);
    }

    @Override
    public IURLGetter apply(MockCredential credential) {
        return null;
    }
}
