package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppBinaryServiceConfig {

    Logger logger = LoggerFactory.getLogger(AppBinaryServiceConfig.class);

    @Value("${custom_app.core_pool_size}")
    private int corePoolSize;

    @Value("${custom_app.max_pool_size}")
    private int maxPoolSize;

    @Value("${custom_app.queue_capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        logger.info(String.format("ThreadPoolTaskExecutor bean instantiated, corePoolSize %d, maxPoolSize %d", corePoolSize, maxPoolSize));
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity); // reject if queuecapacity hit
        executor.setThreadNamePrefix("CustomThreadPoolTaskExecutor-");
        executor.initialize();
        return executor;
    }
}
