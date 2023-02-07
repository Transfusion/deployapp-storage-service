package io.github.transfusion.deployapp.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class AMQPConfig {
    public static final String EXCHANGE_NAME = "deployapp-exchange";
    public static final String INTEGRATION_EVENTS_QUEUE_NAME = "integration_events";

    @Bean
    public TopicExchange deployAppExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    public static String INTEGRATION_EVENTS_ROUTING_KEY = "ievent";

    @Bean
    public Queue integrationEventsQueue() {
        return new Queue(INTEGRATION_EVENTS_QUEUE_NAME);
    }

    @Bean
    public Binding declareIntegrationEventsBinding() {
        return BindingBuilder.bind(integrationEventsQueue())
                .to(deployAppExchange()).with(INTEGRATION_EVENTS_ROUTING_KEY);
    }

    public static final String FANOUT_EXCHANGE_NAME = "deployapp-exchange-fanout";

    public String getFANOUT_EVENTS_QUEUE_NAME() {
        return FANOUT_EVENTS_QUEUE_NAME;
    }

    public static final String FANOUT_EVENTS_QUEUE_NAME = "dpl-str-" + UUID.randomUUID();

    @Bean("deployAppExchangeFanout")
    public FanoutExchange deployAppExchangeFanout() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean("fanoutEventsQueue")
    public Queue fanoutEventsQueue() {
        return QueueBuilder.nonDurable(FANOUT_EVENTS_QUEUE_NAME).autoDelete().build();
    }

    @Bean
    public Binding declareFanoutEventsBinding() {
        return BindingBuilder.bind(fanoutEventsQueue())
                .to(deployAppExchangeFanout());
    }


}
