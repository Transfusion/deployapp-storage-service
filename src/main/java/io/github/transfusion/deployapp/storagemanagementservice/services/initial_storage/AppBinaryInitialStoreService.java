package io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.transfusion.deployapp.auth.CustomUserPrincipal;
import io.github.transfusion.deployapp.session.SessionData;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryStoreJob;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryStoreJobRepository;
//import io.github.transfusion.deployapp.storagemanagementservice.services.AMQPBindingsService;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageService;
import io.github.transfusion.deployapp.storagemanagementservice.services.storage.IUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Service
public class AppBinaryInitialStoreService {

    Logger logger = LoggerFactory.getLogger(AppBinaryInitialStoreService.class);

    public enum InitialStoreStatus {
        PROCESSING("P"), ABORTED("A"), CANCELLING("C"), SUCCESSFUL("S");

        private final String code;

        private InitialStoreStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @Converter(autoApply = true)
    public static class InitialStoreStatusConverter implements AttributeConverter<InitialStoreStatus, String> {

        @Override
        public String convertToDatabaseColumn(InitialStoreStatus attribute) {
            if (attribute == null) return null;
            return attribute.getCode();
        }

        @Override
        public InitialStoreStatus convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;

            return Stream.of(InitialStoreStatus.values())
                    .filter(c -> c.getCode().equals(dbData))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }

    @Autowired
    private AppBinaryStoreJobRepository appBinaryStoreJobRepository;

    @Autowired
    private SessionData sessionData;

    public List<AppBinaryStoreJob> findOwnJobsAnonymous() {
        return appBinaryStoreJobRepository.findAllById(sessionData.getAnonymousAppBinaries());
    }

    public List<AppBinaryStoreJob> findOwnJobs() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        UUID userId = ((CustomUserPrincipal) authentication.getPrincipal()).getId();

        return appBinaryStoreJobRepository.findByAppBinary_UserId(userId);
    }

    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;

    @Autowired
    @Qualifier("customRetryTemplate")
    private RetryTemplate retryTemplate;

    //    private final Map<UUID, Future<Integer>> jobs = new ConcurrentHashMap<>();

    /**
     * Map of job ID to a runnable that CANCELS the upload in progress;
     */
    private final Map<UUID, Runnable> jobs = new ConcurrentHashMap<>();

//    @Autowired
//    private AsyncWrapperService asyncService;

    @Autowired
    private StorageService storageService;

//    @Autowired
//    private AMQPBindingsService amqpBindingsService;

    public AppBinaryStoreJob storeAppBinary(AppBinary appBinary,
                                            UUID storageCredentialId,
                                            Instant credentialCreatedOn,
                                            String name, File object) throws JsonProcessingException {
        AppBinaryStoreJob storeJob = new AppBinaryStoreJob();
        storeJob.setAppBinary(appBinary);
        storeJob.setCreatedDate(Instant.now());
        storeJob.setStatus(InitialStoreStatus.PROCESSING);

        AppBinaryStoreJob job = appBinaryStoreJobRepository.save(storeJob);
        // fire off async retryable
//        CompletableFuture<Void> future = asyncService.uploadPrivateAppBinaryObject(jobId, storageCredentialId, credentialCreatedOn, appBinary.getId(),
//                name, object);

        UUID jobId = job.getId();
        UUID appBinaryId = appBinary.getId();
//        amqpBindingsService.addDirectBinding(jobId);

        IUploader uploader = storageService.resolveUploader(storageCredentialId,
                credentialCreatedOn);

        Future<Integer> fut = asyncTaskExecutor.submit(() -> {
            return retryTemplate.execute(context -> {
                logger.info(String.format("starting initial store of appbinary %s", appBinaryId));
                if (transactionalWrapperService.isJobCancelling(jobId)) {
                    transactionalWrapperService.updateStoreJob(jobId,
                            InitialStoreStatus.ABORTED);
                    object.delete();
                    jobs.remove(jobId);
//                    amqpBindingsService.removeDirectBinding(jobId);
//            return CompletableFuture.completedFuture(null);
                    return -1;
                }

                logger.info(String.format("starting initial upload of appbinary %s", appBinaryId));
                uploader.uploadPrivateAppBinaryObject(
                        /*storageCredentialId,
                        credentialCreatedOn,*/
                        appBinaryId, name, object);

                // successfully uploaded, delete job(?), mark job as completed (because of foreign key 1 to 1)
                transactionalWrapperService.updateStoreJob(jobId,
                        InitialStoreStatus.SUCCESSFUL);

                logger.info(String.format("async storage of %s name %s succeeded", jobId, name));
                object.delete();
                jobs.remove(jobId);
//                amqpBindingsService.removeDirectBinding(jobId);
                return -1;
            }, context -> {
                logger.error("Recovering from {}", context.getLastThrowable().getMessage());
                context.getLastThrowable().printStackTrace();
                transactionalWrapperService.updateStoreJob(jobId,
                        InitialStoreStatus.ABORTED);
                object.delete();
                // clean up from within the final handler
                jobs.remove(jobId);
//                amqpBindingsService.removeDirectBinding(jobId);
                return -1;
            });
        });

//        jobs.put(jobId, fut);
        jobs.put(jobId, () -> {
            uploader.abort();
            fut.cancel(true);
        });

        return job;
    }

    @Autowired
    private TransactionalWrapperService transactionalWrapperService;

    public void cancelStoreAppBinaryFuture(UUID jobId) {
        if (jobs.containsKey(jobId)) jobs.get(jobId).run(); // runs in current thread
    }

    public void cancelStoreAppBinary(UUID jobId) {
        // always attempt to cancel regardless of what the database state is
        AppBinaryStoreJob job = transactionalWrapperService.getReferenceById(jobId);
        if (job.isProcessing())
//            throw new IllegalArgumentException(String.format("%s is not in PROCESSING status", jobId));
            transactionalWrapperService.cancelStoreJob(jobId);
    }

    public void deleteStoreAppBinary(UUID jobId) {
        AppBinaryStoreJob job = transactionalWrapperService.getReferenceById(jobId);
        if (!Arrays.asList(InitialStoreStatus.CANCELLING, InitialStoreStatus.SUCCESSFUL)
                .contains(job.getStatus())) {
            throw new IllegalArgumentException(String.format("AppBinaryStoreJob with id %s is not in CANCELLING or SUCCESSFUL state.", jobId));
        }

        // only allow deletion if it has been stuck in CANCELLING for more than one hour
        if (job.getStatus() == InitialStoreStatus.CANCELLING &&
                ChronoUnit.MINUTES.between(job.getCreatedDate(), Instant.now()) < 60
        )
            throw new IllegalArgumentException(String.format("AppBinaryStoreJob with id %s in CANCELLING state is not ready to be deleted yet.", jobId));
        transactionalWrapperService.deleteStoreJob(job.getId());
    }

    // to be run on application startup. all pods will be taken offline in the event of an upgrade.
//    @EventListener
//    public void appReady(ApplicationReadyEvent event) {
    public void cleanupStaleJobs() {
        appBinaryStoreJobRepository.bulkUpdateStatus(InitialStoreStatus.PROCESSING, InitialStoreStatus.ABORTED);
        appBinaryStoreJobRepository.bulkUpdateStatus(InitialStoreStatus.CANCELLING, InitialStoreStatus.ABORTED);
        logger.info("done initial AppBinaryStoreJob cleanup");
    }
}
