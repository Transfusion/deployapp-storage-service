package io.github.transfusion.deployapp.storagemanagementservice.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.transfusion.deployapp.messaging.AMQPConfig.FANOUT_EXCHANGE_NAME;

@Service
public class FanoutEventsSender {
    @Autowired
    private RabbitTemplate template;

    public void send(String key, Object o) {
        template.convertAndSend(FANOUT_EXCHANGE_NAME, key, o);
    }
}
