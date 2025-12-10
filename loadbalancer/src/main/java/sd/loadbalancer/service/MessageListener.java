package sd.loadbalancer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import sd.loadbalancer.dtos.MeasurementEvent;

import static sd.loadbalancer.config.RabbitMQConfig.INPUT_QUEUE;

@Service
public class MessageListener {

    private final DataRouterService routerService;

    public MessageListener(DataRouterService routerService) {
        this.routerService = routerService;
    }

    @RabbitListener(queues = INPUT_QUEUE)
    public void handleDeviceMeasurement(MeasurementEvent event) {
        if (event != null) {
            System.out.println(event.getDeviceId() + " " + event.getValue() + "\n\n");
            routerService.routeEvent(event);
        }
    }
}
