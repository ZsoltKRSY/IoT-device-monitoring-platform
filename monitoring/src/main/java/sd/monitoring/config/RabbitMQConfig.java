package sd.monitoring.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sd.monitoring.dtos.MeasurementEvent;
import sd.monitoring.dtos.SyncEvent;
import sd.monitoring.services.ReplicaIdService;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String SYNC_QUEUE = "sync.queue.monitoring";

    public static final String OVERCONSUMPTION_EXCHANGE = "overconsumption.exchange";
    public static final String OVERCONSUMPTION_QUEUE = "overconsumption.queue.monitoring";

    public static final String INGEST_EXCHANGE = "monitoring.ingest.exchange";
    public static final String INGEST_QUEUE_BASE = "ingest.replica.";

    private final int claimedReplicaId;

    public RabbitMQConfig(ReplicaIdService replicaIdService) {
        this.claimedReplicaId = replicaIdService.findAndClaimId();
    }

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
    public DirectExchange ingestExchange() {
        return new DirectExchange(INGEST_EXCHANGE, false, false);
    }

    @Bean
    public Binding bindingIngestionQueue(DirectExchange ingestExchange, @Qualifier("ingestionQueue") Queue ingestionQueue) {
        String queueName = INGEST_QUEUE_BASE + claimedReplicaId;

        return BindingBuilder.bind(ingestionQueue)
                .to(ingestExchange)
                .with(queueName);
    }

    @Bean
    public Queue ingestionQueue() {
        String queueName = INGEST_QUEUE_BASE + claimedReplicaId;
        System.out.println("Defining unique queue: " + queueName);

        return new Queue(queueName, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("sd.*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("sd.authentication.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.devices.dtos.SyncEvent", SyncEvent.class);
        idClassMapping.put("sd.loadbalancer.dtos.MeasurementEvent", MeasurementEvent.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean("dynamicReplicaId")
    public Integer dynamicReplicaId() {
        return claimedReplicaId;
    }

}