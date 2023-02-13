package io.github.transfusion.deployapp.storagemanagementservice.messaging;

import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnFailedToAcquireMutexEvent;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Component;

@Component
public class LeaderEventsListener {

    Logger logger = LoggerFactory.getLogger(LeaderEventsListener.class);

    private boolean firstRun = true;

    @Autowired
    private AppBinaryInitialStoreService appBinaryInitialStoreService;

    @EventListener(OnGrantedEvent.class)
    public void leadershipGranted(OnGrantedEvent evt) {
        logger.info(String.format("Leadership Granted %s", firstRun));
        if (firstRun) appBinaryInitialStoreService.cleanupStaleJobs();
    }

    @EventListener(OnRevokedEvent.class)
    public void leadershipRevoked(OnRevokedEvent evt) {
        logger.info("Leadership Revoked");
        firstRun = false;
    }

    @EventListener(OnFailedToAcquireMutexEvent.class)
    public void leadershipFailedToAcquire(OnFailedToAcquireMutexEvent evt) {
        firstRun = false;
    }
}
