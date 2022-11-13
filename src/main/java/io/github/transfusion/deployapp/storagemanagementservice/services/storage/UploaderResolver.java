package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploaderResolver implements IUploaderResolver {

//    @Autowired
//    private S3Uploader s3Uploader;

    @Override
    public IUploader apply(S3Credential credential) {
        return new S3Uploader(credential);
    }

    @Override
    public IUploader apply(FtpCredential credential) {
        return new FtpUploader(credential);
    }
}
