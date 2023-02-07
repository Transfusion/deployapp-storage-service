package io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryStoreJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionalWrapperService {
    Logger logger = LoggerFactory.getLogger(TransactionalWrapperService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AppBinaryStoreJobRepository appBinaryStoreJobRepository;

    @Transactional
    public void updateStoreJob(UUID appBinaryStoreJobId, AppBinaryInitialStoreService.InitialStoreStatus status) {
        AppBinaryStoreJob storeJob = appBinaryStoreJobRepository.getReferenceById(appBinaryStoreJobId);
        storeJob.setStatus(status);
        appBinaryStoreJobRepository.save(storeJob);
    }

    @Transactional
    public void cancelStoreJob(UUID appBinaryStoreJobId) {
        Optional<AppBinaryStoreJob> _job = appBinaryStoreJobRepository.findById(appBinaryStoreJobId);
        if (_job.isPresent()) {
            AppBinaryStoreJob job = _job.get();
            job.setStatus(AppBinaryInitialStoreService.InitialStoreStatus.CANCELLING);
            appBinaryStoreJobRepository.save(job);
        }
    }

    public AppBinaryStoreJob getReferenceById(UUID appBinaryStoreJobId) {
        return appBinaryStoreJobRepository.getReferenceById(appBinaryStoreJobId);
    }

    @Transactional
    public boolean isJobCancelling(UUID appBinaryStoreJobId) {
        return appBinaryStoreJobRepository.getReferenceById(appBinaryStoreJobId).isCancelling();
    }
}
