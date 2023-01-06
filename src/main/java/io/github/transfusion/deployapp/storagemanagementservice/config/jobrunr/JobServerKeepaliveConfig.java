package io.github.transfusion.deployapp.storagemanagementservice.config.jobrunr;

import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@Profile({"!db-test"})
public class JobServerKeepaliveConfig {

    Logger logger = LoggerFactory.getLogger(JobServerKeepaliveConfig.class);

    @Autowired
    private JobScheduler jobScheduler;
    @Autowired
    private BackgroundJobServer backgroundJobServer;

    /**
     * See <a href="https://github.com/jobrunr/jobrunr/issues/498">JobRunr GH Issue #498</a>
     */
    @Scheduled(fixedRateString = "${custom_jobrunr.keepalive-interval}")
    private void checkBackgroundServerRunning() {
        if (!backgroundJobServer.isRunning()) {
            logger.error("JobRunr background server is not running since {}", backgroundJobServer.getServerStatus().getLastHeartbeat());
//            if (backgroundJobServer.getServerStatus().getLastHeartbeat().isBefore(Instant.now().plus(JOB_SERVER_STALE_LIMIT_DURATION))) {
            logger.warn("Look like JobRunr background server is not running and stuck for a while, try to restart ...");
            backgroundJobServer.start();
//            }
        }
    }


//    @Autowired
//    private JobScheduler jobScheduler;
//
//    @Autowired
//    private BackgroundJobServer backgroundJobServer;
//
//    private static final Duration JOB_SERVER_STALE_LIMIT_DURATION = Duration.ofMinutes(1);
//
//    public void schedule(final UUID jobId, final Instant scheduledAt, final JobLambda job) {
//        try {
//            checkBackgroundServerRunning();
//            jobScheduler.schedule(jobId, scheduledAt, job);
//        } catch (final Exception e) {
//            logger.error("Unable to schedule job with jobId {}", jobId, e);
//            throw e;
//        }
//    }
//
//    private void checkBackgroundServerRunning() {
//        if (!backgroundJobServer.isRunning()) {
//            logger.error("JobRunr background server is not running since {}", backgroundJobServer.getServerStatus().getLastHeartbeat());
//            if (backgroundJobServer.getServerStatus().getLastHeartbeat().isBefore(Instant.now().plus(JOB_SERVER_STALE_LIMIT_DURATION))) {
//                logger.warn("Look like JobRunr background server is not running and stuck for a while, try to restart ...");
//                backgroundJobServer.start();
//            }
//        }
//    }
//
//    public void delete(final UUID jobId, final String reason) {
//        try {
//            checkBackgroundServerRunning();
//            jobScheduler.delete(jobId, reason);
//        } catch (final Exception e) {
//            logger.error("Unable to delete scheduled job with id {}", jobId, e);
//        }
//    }
}
