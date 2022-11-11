package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import org.springframework.stereotype.Service;

@Service
public class DeleterResolver implements IDeleterResolver {
    @Override
    public IDeleter apply(S3Credential s3Credential) {
        return new S3Deleter(s3Credential);
    }

    @Override
    public IDeleter apply(FtpCredential ftpCredential) {
        return null;
    }
}
