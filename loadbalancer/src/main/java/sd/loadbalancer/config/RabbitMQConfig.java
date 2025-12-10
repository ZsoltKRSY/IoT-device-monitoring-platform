package sd.loadbalancer.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INPUT_QUEUE = "device.data.input";
    public static final String INGEST_EXCHANGE = "monitoring.ingest.exchange";

    @Value("${loadbalancer.monitoring.replicas:5}")
    @Getter
    private int replicaCount;

    @Bean
    public Queue inputQueue() {
        return new Queue(INPUT_QUEUE, false);
    }

    @Bean
    public DirectExchange ingestExchange() {
        return new DirectExchange(INGEST_EXCHANGE, false, false);
    }

    @Bean
    public Queue[] ingestQueues() {
        Queue[] queues = new Queue[replicaCount];
        for (int i = 0; i < replicaCount; i++) {
            queues[i] = new Queue("ingest.replica." + (i + 1), false);
        }
        return queues;
    }

    @Bean
    public Binding[] ingestBindings(DirectExchange ingestExchange, Queue[] ingestQueues) {
        Binding[] bindings = new Binding[replicaCount];
        for (int i = 0; i < replicaCount; i++) {
            bindings[i] = BindingBuilder.bind(ingestQueues[i])
                    .to(ingestExchange)
                    .with(ingestQueues[i].getName());
        }
        return bindings;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}