package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;
import org.springframework.stereotype.Service;

@Service
public class DownloaderResolver implements IDownloaderResolver {

    @Override
    public IDownloader apply(S3Credential s3Credential) {
        return new S3Downloader(s3Credential);
    }

    @Override
    public IDownloader apply(FtpCredential ftpCredential) {
        return null;
    }
}
