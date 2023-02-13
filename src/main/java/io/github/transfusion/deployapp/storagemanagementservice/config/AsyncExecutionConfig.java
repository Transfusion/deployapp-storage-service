package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AsyncExecutionConfig {

    static class CustomRetryListener extends RetryListenerSupport {

        Logger logger = LoggerFactory.getLogger(CustomRetryListener.class);

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            logger.warn("Retrying...");
            context.getLastThrowable().printStackTrace();
            super.onError(context, callback, throwable);
        }
    }

    Logger logger = LoggerFactory.getLogger(AsyncExecutionConfig.class);

    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        logger.info("AsyncTaskExecutor bean instantiated");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("CustomExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean("customRetryTemplate")
    public RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(Exception.class, true);
        retryableExceptions.put(InterruptedException.class, false);
        retryableExceptions.put(UncheckedIOException.class, false);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, retryableExceptions);
        retryPolicy.setMaxAttempts(3);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1500); // 1.5 seconds

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
//        must be registered or else retries wont be called
        template.registerListener(new CustomRetryListener());

        return template;
    }
}
