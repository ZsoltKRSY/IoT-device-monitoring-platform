package sd.websocket.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sd.websocket.dtos.OverconsumptionDetailedEvent;

@Service
public class OverconsumptionService {

    private final SimpMessagingTemplate messagingTemplate;

    public OverconsumptionService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendOverconsumptionDetails(OverconsumptionDetailedEvent event) {
        messagingTemplate.convertAndSend("/queue/user-" + event.getUserId() + "/overconsumption", event);
    }

}
