package sd.devices.dtos.mappers;

import sd.devices.dtos.DeviceDetailsDTO;
import sd.devices.dtos.DeviceOperationDTO;
import sd.devices.entities.Device;

public class DeviceMapper {

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return DeviceDetailsDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .maxConsumption(device.getMaxConsumption())
                .manufacturer(device.getManufacturer())
                .model(device.getModel())
                .description(device.getDescription())
                .userId(device.getOwner() != null ? device.getOwner().getId() : null)
                .build();
    }

    public static Device toDeviceEntity(DeviceOperationDTO deviceOperationDTO) {
        return Device.builder()
                .name(deviceOperationDTO.getName())
                .maxConsumption(deviceOperationDTO.getMaxConsumption())
                .manufacturer(deviceOperationDTO.getManufacturer())
                .model(deviceOperationDTO.getModel())
                .description(deviceOperationDTO.getDescription())
                .build();
    }

}
