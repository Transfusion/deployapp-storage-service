package io.github.transfusion.deployapp.storagemanagementservice.messaging;

import io.github.transfusion.deployapp.dto.internal.DeleteStorageCredentialEvent;
import io.github.transfusion.deployapp.dto.internal.TestMessage;
import io.github.transfusion.deployapp.dto.response.S3CredentialDTO;
import io.github.transfusion.deployapp.storagemanagementservice.services.StorageCredsUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.transfusion.deployapp.messaging.AMQPConfig.INTEGRATION_EVENTS_QUEUE_NAME;

@Service
@RabbitListener(queues = INTEGRATION_EVENTS_QUEUE_NAME)
public class IntegrationEventsListener {
    Logger logger = LoggerFactory.getLogger(IntegrationEventsListener.class);

    @RabbitHandler
    public void receiveTestMessage(TestMessage testMessage) {
        logger.info("Received test message! {}", testMessage.toString());
    }

    @Autowired
    private StorageCredsUpdateService storageCredsUpdateService;

    @RabbitHandler
    public void receiveS3CredentialDTO(S3CredentialDTO s3CredentialDTO) {
        storageCredsUpdateService.createOrUpdateCredential(s3CredentialDTO);
    }

    @RabbitHandler
    public void receiveDeleteStorageCredentialEvent(DeleteStorageCredentialEvent event) {
        storageCredsUpdateService.deleteCredential(event.getId());
    }

}