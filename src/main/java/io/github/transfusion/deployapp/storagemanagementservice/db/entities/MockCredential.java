package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "mock_credentials")
@PrimaryKeyJoinColumn(name = "id")
public class MockCredential extends StorageCredential {
    @Override
    public IUploader resolveUploader(IUploaderResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IDownloader resolveDownloader(IDownloaderResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IURLGetter resolveURLGetter(IURLGetterResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IDeleter resolveDeleter(IDeleterResolver resolver) {
        return resolver.apply(this);
    }
}
