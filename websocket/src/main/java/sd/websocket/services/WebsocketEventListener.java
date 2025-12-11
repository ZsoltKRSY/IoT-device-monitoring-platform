package sd.websocket.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import sd.websocket.dtos.OverconsumptionDetailedEvent;
import sd.websocket.dtos.SyncEvent;

import static sd.websocket.config.RabbitMQConfig.OVERCONSUMPTION_DETAILED_QUEUE;

@Service
public class WebsocketEventListener {
    private final OverconsumptionService overconsumptionService;
    private final ObjectMapper objectMapper;

    public WebsocketEventListener(OverconsumptionService overconsumptionService, ObjectMapper objectMapper) {
        this.overconsumptionService = overconsumptionService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = OVERCONSUMPTION_DETAILED_QUEUE)
    public void handleSyncEvent(SyncEvent event) {
        try {
            OverconsumptionDetailedEvent details = objectMapper.readValue(
                    event.payload(),
                    OverconsumptionDetailedEvent.class
            );
            overconsumptionService.sendOverconsumptionDetails(details);
        } catch (Exception e) {
            System.err.println("Error processing sync event: " + e.getMessage());
        }
    }
}