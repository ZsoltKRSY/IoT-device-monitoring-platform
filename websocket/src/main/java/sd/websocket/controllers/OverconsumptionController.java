package sd.websocket.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sd.websocket.dtos.OverconsumptionDetailedEvent;
import sd.websocket.services.OverconsumptionService;

@RestController
public class OverconsumptionController {

    private final OverconsumptionService overconsumptionService;

    public OverconsumptionController(OverconsumptionService overconsumptionService) {
        this.overconsumptionService = overconsumptionService;
    }

    @PostMapping("/broadcast")
    public void broadcastOverconsumptionEvent(@RequestBody OverconsumptionDetailedEvent event) {
        overconsumptionService.sendOverconsumptionDetails(event);
    }
}
