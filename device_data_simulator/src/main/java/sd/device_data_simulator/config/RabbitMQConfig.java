package sd.device_data_simulator.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DATA_INPUT_EXCHANGE = "device.data.exchange";
    public static final String LBS_INPUT_QUEUE = "device.data.input";
    public static final String LBS_ROUTING_KEY = "measurements";

    @Bean
    public DirectExchange dataInputExchange() {
        return new DirectExchange(DATA_INPUT_EXCHANGE, false, false);
    }

    @Bean
    public Queue lbsInputQueue() {
        return new Queue(LBS_INPUT_QUEUE, false);
    }

    @Bean
    public Binding bindingLbsInputQueue(DirectExchange dataInputExchange, Queue lbsInputQueue) {
        return BindingBuilder.bind(lbsInputQueue)
                .to(dataInputExchange)
                .with(LBS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
