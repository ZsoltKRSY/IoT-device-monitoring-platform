package sd.monitoring.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import sd.monitoring.dtos.DeviceEvent;
import sd.monitoring.dtos.MeasurementEvent;
import sd.monitoring.dtos.SyncEvent;

import static sd.monitoring.config.RabbitMQConfig.SYNC_QUEUE;

@Service
public class ConsumptionEventListener {
    private final DeviceService deviceService;
    private final ConsumptionService consumptionService;
    private final ObjectMapper objectMapper;

    public ConsumptionEventListener(DeviceService deviceService, ConsumptionService consumptionService, ObjectMapper objectMapper) {
        this.deviceService = deviceService;
        this.consumptionService = consumptionService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = SYNC_QUEUE)
    public void handleSyncEvent(SyncEvent event) {
        try {
            switch (event.eventType()) {
                case "DEVICE_CREATED":
                    DeviceEvent deviceCreatedEvent = objectMapper.readValue(
                            event.payload(),
                            DeviceEvent.class
                    );
                    deviceService.createDevice(deviceCreatedEvent);
                    break;

                case "DEVICE_UPDATED":
                    DeviceEvent deviceUpdatedEvent = objectMapper.readValue(
                            event.payload(),
                            DeviceEvent.class
                    );
                    deviceService.updateDevice(deviceUpdatedEvent);
                    break;

                case "DEVICE_DELETED":
                    DeviceEvent deviceDeletedEvent = objectMapper.readValue(
                            event.payload(),
                            DeviceEvent.class
                    );
                    deviceService.deleteDevice(deviceDeletedEvent.getDeviceId());
                    break;

                case "MEASUREMENT_EVENT":
                    MeasurementEvent measurementEvent = objectMapper.readValue(
                            event.payload(),
                            MeasurementEvent.class
                    );
                    consumptionService.processMeasurement(measurementEvent);
                    break;

                default:
                    System.out.println("Ignored event: " + event.eventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing sync event: " + e.getMessage());
        }
    }

}
