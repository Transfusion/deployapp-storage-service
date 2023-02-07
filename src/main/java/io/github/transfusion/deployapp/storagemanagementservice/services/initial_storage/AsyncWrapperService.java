package io.github.transfusion.deployapp.storagemanagementservice.services;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryStoreJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

@Service
public class AppBinaryInitialStoreAsyncService {

    Logger logger = LoggerFactory.getLogger(AppBinaryInitialStoreAsyncService.class);

    @Autowired
    private AppBinaryStoreJobRepository appBinaryStoreJobRepository;

    @Autowired
    private StorageService storageService;

    @Async
    @Retryable(value = Exception.class)
    public void uploadPrivateAppBinaryObject(UUID appBinaryStoreJobId,
                                             UUID storageCredentialId,
                                             Instant credentialCreatedOn,
                                             UUID appBinaryId, String name, File object) throws Exception {

        AppBinaryStoreJob storeJob = appBinaryStoreJobRepository.getReferenceById(appBinaryStoreJobId);
        storageService.uploadPrivateAppBinaryObject(storageCredentialId,
                credentialCreatedOn,
                appBinaryId, name, object);
        // successfully uploaded, delete job(?), mark job as completed (because of foreign key 1 to 1)
        storeJob.setStatus(AppBinaryInitialStoreService.InitialStoreStatus.SUCCESSFUL);
        logger.info(String.format("async storage of %s name %s succeeded", appBinaryStoreJobId, name));
        appBinaryStoreJobRepository.save(storeJob);
        object.delete();
    }

    @Recover
    void recover(Exception e,
                 UUID appBinaryStoreJobId,
                 UUID storageCredentialId,
                 Instant credentialCreatedOn,
                 UUID appBinaryId, String name, File object) {
        // mark job as failed.
        AppBinaryStoreJob storeJob = appBinaryStoreJobRepository.getReferenceById(appBinaryStoreJobId);
        storeJob.setStatus(AppBinaryInitialStoreService.InitialStoreStatus.ABORTED);
        appBinaryStoreJobRepository.save(storeJob);
        object.delete();
    }
}
