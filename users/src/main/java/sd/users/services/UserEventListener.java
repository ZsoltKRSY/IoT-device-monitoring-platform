package sd.users.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import sd.users.dtos.SyncEvent;
import sd.users.dtos.UserIdEvent;
import sd.users.dtos.UserOperationEvent;

import static sd.users.config.RabbitMQConfig.SYNC_QUEUE;

@Service
public class UserEventListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserEventListener(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
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

                case "USER_UPDATED":
                    UserOperationEvent userUpdatedEvent = objectMapper.readValue(
                            event.payload(),
                            UserOperationEvent.class
                    );
                    userService.updateUser(userUpdatedEvent.getUserId(), userUpdatedEvent);
                    break;

                case "USER_DELETED":
                    UserIdEvent userDeletedEvent = objectMapper.readValue(
                            event.payload(),
                            UserIdEvent.class
                    );
                    userService.deleteUser(userDeletedEvent.getUserId());
                    break;

                default:
                    //System.out.println("Ignored event: " + event.eventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing sync event: " + e.getMessage());
        }
    }
}
