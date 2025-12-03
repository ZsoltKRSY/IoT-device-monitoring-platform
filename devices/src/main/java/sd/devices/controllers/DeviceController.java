package sd.devices.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.devices.dtos.DeviceDetailsDTO;
import sd.devices.dtos.DeviceOperationDTO;
import sd.devices.services.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/devices")
@Tag(
        name = "Devices",
        description = "Endpoints for managing devices, including creation, updates, deletion, and user-device associations."
)
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Operation(
            summary = "Get all devices",
            description = "Fetches a list of all devices stored in the system, regardless of user ownership."
    )
    @GetMapping
    public ResponseEntity<List<DeviceDetailsDTO>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @Operation(
            summary = "Get all device IDs",
            description = "Fetches a list of IDs of all devices stored in the system, regardless of user ownership."
    )
    @GetMapping("/all-ids")
    public ResponseEntity<List<Long>> getAllDeviceIds() {
        return ResponseEntity.ok(deviceService.getAllDeviceIds());
    }

    @Operation(
            summary = "Get all devices of a user",
            description = "Retrieves all devices associated with a specific user by their user ID."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceDetailsDTO>> getAllDevicesOfUser(@PathVariable Long userId) {
        return ResponseEntity.ok(deviceService.getAllDevicesOfUser(userId));
    }

    @Operation(
            summary = "Get device by ID",
            description = "Retrieves detailed information for a specific device by its unique ID."
    )
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceDetailsDTO> getDeviceById(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceService.getDeviceById(deviceId));
    }

    @Operation(
            summary = "Create new device",
            description = "Registers a new device with its details, optionally assigning it to a user."
    )
    @PostMapping
    public ResponseEntity<DeviceDetailsDTO> createDevice(@RequestBody DeviceOperationDTO deviceOperationDTO) {
        return ResponseEntity.ok(deviceService.createDevice(deviceOperationDTO));
    }

    @Operation(
            summary = "Update device information",
            description = "Updates the details of an existing device based on its ID."
    )
    @PutMapping("/{deviceId}")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(@PathVariable Long deviceId, @RequestBody DeviceOperationDTO deviceOperationDTO) {
        return ResponseEntity.ok(deviceService.updateDevice(deviceId, deviceOperationDTO));
    }

    @Operation(
            summary = "Delete device",
            description = "Removes a specific device from the system by its ID."
    )
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove user from all devices",
            description = "Disassociates a user from all devices they own or are linked to. Used when deleting a user account."
    )
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeUserFromDevices(@PathVariable Long userId) {
        deviceService.removeUserFromDevices(userId);
        return ResponseEntity.ok().build();
    }
}
