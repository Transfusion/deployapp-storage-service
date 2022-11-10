package io.github.transfusion.deployapp.storagemanagementservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.transfusion.deployapp.dto.response.StorageCredentialDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.StorageCredentialRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.StorageCredentialMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageCredsUpdateService {

    Logger logger = LoggerFactory.getLogger(StorageCredsUpdateService.class);

    @Autowired
    private StorageCredentialRepository storageCredentialRepository;

    @Autowired
    @Qualifier("MainServiceWebClient")
    private WebClient mainServiceWebClient;

    @Autowired
    private StorageCredentialMapper mapper;

    @Autowired
    private StorageCredentialRepository repository;

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;


    /**
     * Checks whether the given credential exists in the local replica, and if not loads and caches it from the main service
     * N.B. created_on should be thought of as last updated; it gets set to NOW() whenever it is changed
     *
     * @param id uuid of the credential
     * @return {@link io.github.transfusion.deployapp.storagemanagementservice.db.entities.StorageCredential} if one exists
     */
    public StorageCredential getCredential(UUID id, Instant createdOn) throws JsonProcessingException {
        Optional<StorageCredential> cred = storageCredentialRepository.findById(id);
        if (cred.isEmpty() || cred.get().getCreatedOn().isBefore(createdOn)) {
            // attempt to fetch from main microservice
            String path = String.format("/microservice-api/v1/credentials/%s", id);
            /* By default, 4xx and 5xx responses result in a WebClientResponseException.
            https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.RequestHeadersSpec.html#retrieve-- */
            Mono<JsonNode> respMono = mainServiceWebClient.get().uri(path).retrieve().bodyToMono(JsonNode.class).single();
//                    .doOnEach(jsonNodeSignal -> {
//
//                    })
//                    .switchIfEmpty(Mono.error(new IllegalArgumentException(String.format("Storage credential with UUID %s does not exist", id))));

            JsonNode jsonNode = respMono.block();
            StorageCredentialDTO dto = mapper.fromJsonNode(mapperBuilder.build(), jsonNode);
            StorageCredential credential = mapper.mapStorageCredentialFromDTO(dto);

            repository.save(credential);
            return credential;
        } else {
            return cred.get();
        }
    }


    @Autowired
    private StorageCredentialMapper storageCredentialMapper;

    public void createOrUpdateCredential(StorageCredentialDTO credentialDTO) {
        StorageCredential storageCredential = storageCredentialMapper.mapStorageCredentialFromDTO(credentialDTO);
        logger.info("Update storage credential {}", storageCredential);
        storageCredentialRepository.save(storageCredential);
    }

    public void deleteCredential(UUID id) {
        try {
            storageCredentialRepository.deleteById(id);
            logger.info("Deleted storage credential {}", id);
        } catch (Exception e) {
            logger.info("Exception occurred when trying to delete storage credential {}", id);
            e.printStackTrace();
        }
    }

}
