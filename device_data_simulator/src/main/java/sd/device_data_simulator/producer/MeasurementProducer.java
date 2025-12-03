package sd.device_data_simulator.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import sd.device_data_simulator.dtos.SyncEvent;
import static sd.device_data_simulator.config.RabbitMQConfig.SYNC_EXCHANGE;

@Component
public class MeasurementProducer {

    private final RabbitTemplate rabbitTemplate;

    public MeasurementProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishSyncEvent(String type, String payload) {
        SyncEvent event = new SyncEvent(type, payload);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, "", event);
    }

}
