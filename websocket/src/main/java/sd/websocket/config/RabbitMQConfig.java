package sd.websocket.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sd.websocket.dtos.SyncEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String OVERCONSUMPTION_DETAILED_EXCHANGE = "overconsumption.exchange";
    public static final String OVERCONSUMPTION_DETAILED_QUEUE = "overconsumption.queue.devices";

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
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }

}