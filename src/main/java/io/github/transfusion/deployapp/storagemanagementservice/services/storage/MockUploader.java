package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.MockCredential;

import java.io.File;
import java.util.UUID;

public class MockUploader implements IUploader {

    public MockUploader(MockCredential credential) {

    }

    @Override
    public void uploadPublicAppBinaryObject(UUID appBinaryId, String name, File binary) throws Exception {

    }

    @Override
    public void uploadPrivateAppBinaryObject(UUID appBinaryId, String name, File binary) throws Exception {

    }
}
