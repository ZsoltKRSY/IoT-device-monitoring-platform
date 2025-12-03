package sd.monitoring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.monitoring.dtos.ConsumptionDTO;
import sd.monitoring.services.ConsumptionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/monitoring")
@Tag(name = "Monitoring", description = "Endpoints for device energy consumption management")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @Operation(
            summary = "Get device consumption (by device ID) for a specific day",
            description = "Returns the historical hourly energy consumption of a device for a given day."
    )
    @GetMapping("/{deviceId}")
    public ResponseEntity<List<ConsumptionDTO>> getDeviceConsumptionForDay(@PathVariable Long deviceId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        return ResponseEntity.ok(consumptionService.getDeviceConsumptionForDay(deviceId, day));
    }

}
