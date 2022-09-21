package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Communicates with the deployapp-main microservice
 */
@Configuration
public class MainMicroserviceWebClient {
    @Value("${microservice-endpoints.main}")
    private String mainMSEndpoint;

    @Bean
    WebClient MainServiceWebClient() {
        return WebClient.builder().baseUrl(mainMSEndpoint).build();
    }
}
