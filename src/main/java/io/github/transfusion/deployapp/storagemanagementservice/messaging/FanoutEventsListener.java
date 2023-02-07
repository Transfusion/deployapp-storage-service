package io.github.transfusion.deployapp.storagemanagementservice.messaging;

import io.github.transfusion.deployapp.dto.internal.CancelInitialStoreJobFutureMessage;
import io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage.AppBinaryInitialStoreService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = "#{@AMQPConfig.getFANOUT_EVENTS_QUEUE_NAME()}")
public class FanoutEventsListener {

    @Autowired
    private AppBinaryInitialStoreService appBinaryInitialStoreService;

    @RabbitHandler
    public void receiveCancelInitialStoreJobFutureMessage(CancelInitialStoreJobFutureMessage message) {
        appBinaryInitialStoreService.cancelStoreAppBinaryFuture(message.getId());
    }
}
