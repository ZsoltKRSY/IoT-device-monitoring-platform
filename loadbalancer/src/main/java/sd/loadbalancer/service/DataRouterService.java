package sd.loadbalancer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sd.loadbalancer.config.RabbitMQConfig;
import sd.loadbalancer.dtos.MeasurementEvent;

import static sd.loadbalancer.config.RabbitMQConfig.INGEST_EXCHANGE;

@Service
public class DataRouterService {

    private final RabbitTemplate rabbitTemplate;
    private final int replicaCount;

    public DataRouterService(RabbitTemplate rabbitTemplate, RabbitMQConfig config) {
        this.rabbitTemplate = rabbitTemplate;
        this.replicaCount = config.getReplicaCount();
        System.out.println("LoadBalancer initialized with " + replicaCount + " monitoring replicas.");
    }

    private int getTargetReplicaIndex(Long deviceId) {
        int index = (int) (deviceId % replicaCount);
        return Math.abs(index) + 1;
    }

    public void routeEvent(MeasurementEvent event) {
        if (event.getDeviceId() == null) {
            System.err.println("Cannot route event: deviceId is null.");
            return;
        }

        int replicaIndex = getTargetReplicaIndex(event.getDeviceId());

        String routingKey = "ingest.replica." + replicaIndex;

        rabbitTemplate.convertAndSend(
                INGEST_EXCHANGE,
                routingKey,
                event
        );
    }
}