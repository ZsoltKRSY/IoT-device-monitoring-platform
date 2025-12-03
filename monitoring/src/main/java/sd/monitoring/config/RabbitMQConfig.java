package sd.monitoring.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sd.monitoring.dtos.SyncEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String SYNC_QUEUE = "sync.queue.monitoring";

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
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("sd.*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("sd.authentication.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.devices.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.device_data_simulator.dtos.SyncEvent", SyncEvent.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }
}