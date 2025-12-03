package sd.monitoring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sd.monitoring.dtos.DeviceEvent;
import sd.monitoring.entities.Device;
import sd.monitoring.handlers.models.ResourceNotFoundException;
import sd.monitoring.reporitories.DeviceReporitory;

import java.util.Optional;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceReporitory deviceReporitory;

    public DeviceService(DeviceReporitory deviceReporitory) {
        this.deviceReporitory = deviceReporitory;
    }

    public void createDevice(DeviceEvent deviceCreated) {
        Device device = new Device(deviceCreated.getDeviceId(), deviceCreated.getMaxConsumption());
        Optional<Device> deviceOptional = deviceReporitory.findById(deviceCreated.getDeviceId());
        if (deviceOptional.isPresent()) {
            LOGGER.error("Device id {} is already in the db", deviceCreated.getDeviceId());
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceCreated.getDeviceId());
        }

        device = deviceReporitory.save(device);
        LOGGER.debug("Device id {} was inserted in db", device.getId());
    }

    public void deleteDevice(Long id) {
        deviceReporitory.deleteById(id);
        LOGGER.debug("Device id {} was deleted from the db", id);
    }

}
