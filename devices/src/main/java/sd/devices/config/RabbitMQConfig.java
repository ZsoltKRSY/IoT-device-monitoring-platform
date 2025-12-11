package sd.devices.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sd.devices.dtos.SyncEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String SYNC_QUEUE = "sync.queue.devices";

    public static final String OVERCONSUMPTION_EXCHANGE = "overconsumption.exchange";
    public static final String OVERCONSUMPTION_QUEUE = "overconsumption.queue.monitoring";

    public static final String OVERCONSUMPTION_DETAILED_EXCHANGE = "overconsumption.exchange";
    public static final String OVERCONSUMPTION_DETAILED_QUEUE = "overconsumption.queue.devices";

    @Bean
    public FanoutExchange syncExchange() {
        return new FanoutExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE, false);
    }

    @Bean
    public Binding bindingSyncQueue(FanoutExchange syncExchange, Queue syncQueue) {
        return BindingBuilder.bind(syncQueue).to(syncExchange);
    }

    @Bean
    public DirectExchange overconsumptionExchange() {
        return new DirectExchange(OVERCONSUMPTION_EXCHANGE, false, false);
    }

    @Bean
    public Queue overconsumptionQueue() {
        return new Queue(OVERCONSUMPTION_QUEUE, false);
    }
    
    @Bean
    public Binding bindingOverconsumptionQueue(DirectExchange overconsumptionExchange, Queue overconsumptionQueue) {
        return BindingBuilder.bind(overconsumptionQueue).to(overconsumptionExchange).with(OVERCONSUMPTION_QUEUE);
    }

    @Bean
    public DirectExchange overconsumptionDetailedExchange() {
        return new DirectExchange(OVERCONSUMPTION_DETAILED_EXCHANGE, false, false);
    }

    @Bean
    public Queue overconsumptionDetailedQueue() {
        return new Queue(OVERCONSUMPTION_DETAILED_QUEUE, false);
    }

    @Bean
    public Binding bindingOverconsumptionDetailedQueue(DirectExchange overconsumptionDetailedExchange, Queue overconsumptionDetailedQueue) {
        return BindingBuilder.bind(overconsumptionDetailedQueue).to(overconsumptionDetailedExchange).with(OVERCONSUMPTION_DETAILED_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("sd.*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("sd.authentication.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.devices.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.monitoring.dtos.SyncEvent", SyncEvent.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }
}
