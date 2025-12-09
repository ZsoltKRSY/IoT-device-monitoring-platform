package sd.devices.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import sd.devices.dtos.OverconsumptionEvent;
import sd.devices.dtos.SyncEvent;
import sd.devices.dtos.UserIdEvent;
import sd.devices.dtos.UserOperationEvent;

import static sd.devices.config.RabbitMQConfig.SYNC_QUEUE;

@Service
public class DeviceEventListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final DeviceService deviceService;

    public DeviceEventListener(UserService userService, ObjectMapper objectMapper, DeviceService deviceService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.deviceService = deviceService;
    }

    @RabbitListener(queues = SYNC_QUEUE)
    public void handleSyncEvent(SyncEvent event) {
        try {
            switch (event.eventType()) {
                case "USER_CREATED":
                    UserOperationEvent userCreatedEvent = objectMapper.readValue(
                            event.payload(),
                            UserOperationEvent.class
                    );
                    userService.createUser(userCreatedEvent);
                    break;

                case "USER_DELETED":
                    UserIdEvent userDeletedEvent = objectMapper.readValue(
                            event.payload(),
                            UserIdEvent.class
                    );
                    deviceService.removeUserFromDevices(userDeletedEvent.getUserId());
                    userService.deleteUser(userDeletedEvent.getUserId());
                    break;

                case "OVERCONSUMPTION":
                    OverconsumptionEvent overconsumptionEvent = objectMapper.readValue(
                            event.payload(),
                            OverconsumptionEvent.class
                    );
                    deviceService.manageOverconsumption(overconsumptionEvent);
                    break;

                default:
                    //System.out.println("Ignored event: " + event.eventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing sync event: " + e.getMessage());
        }
    }

}
