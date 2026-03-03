package sd.device_data_simulator.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import sd.device_data_simulator.dtos.MeasurementEvent;

import static sd.device_data_simulator.config.RabbitMQConfig.*;

@Component
public class MeasurementProducer {
    private final RabbitTemplate rabbitTemplate;

    public MeasurementProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMeasurementEvent(MeasurementEvent event) {
        rabbitTemplate.convertAndSend(
                DATA_INPUT_EXCHANGE,
                LBS_ROUTING_KEY,
                event
        );
    }
}
