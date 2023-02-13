package io.github.transfusion.deployapp.storagemanagementservice.config.leader_election;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
@Profile({"!db-test"})
public class LeaderElectionConfig {

    Logger logger = LoggerFactory.getLogger(LeaderElectionConfig.class);

    @Value("${custom_app.lock_registry_key_prefix}")
    private String lockRegistryKeyPrefix;

    @Bean
    public LockRegistry lockRegistry(RedisConnectionFactory factory) {
        return new RedisLockRegistry(factory, lockRegistryKeyPrefix);
    }

    @Bean
    public LockRegistryLeaderInitiator leaderInitiator(LockRegistry lockRegistry) {
        LockRegistryLeaderInitiator leaderInitiator =  new LockRegistryLeaderInitiator(lockRegistry);
        leaderInitiator.setPublishFailedEvents(true);
        return leaderInitiator;
    }

}
