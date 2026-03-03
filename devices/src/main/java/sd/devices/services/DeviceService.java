package sd.devices.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sd.devices.dtos.*;
import sd.devices.dtos.mappers.DeviceMapper;
import sd.devices.entities.Device;
import sd.devices.entities.User;
import sd.devices.handlers.model.ResourceNotFoundException;
import sd.devices.repositories.DeviceRepository;
import sd.devices.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static sd.devices.config.RabbitMQConfig.*;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    private void publishSyncEvent(String type, String payload) {
        SyncEvent event = new SyncEvent(type, payload);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, "", event);
    }

    private void publishOverconsumptionEvent(String type, String payload) {
        SyncEvent event = new SyncEvent(type, payload);
        rabbitTemplate.convertAndSend(OVERCONSUMPTION_DETAILED_EXCHANGE, OVERCONSUMPTION_DETAILED_QUEUE, event);
    }

    public List<DeviceDetailsDTO> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(DeviceMapper::toDeviceDetailsDTO)
                .toList();
    }

    public List<Long> getAllDeviceIds() {
        return deviceRepository.findAll().stream()
                .map(Device::getId)
                .toList();
    }

    public List<DeviceDetailsDTO> getAllDevicesOfUser(Long id) {
        Optional<User> ownerOptional = userRepository.findById(id);
        if (ownerOptional.isEmpty()) {
            LOGGER.error("User id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        return deviceRepository.findAllByOwner(ownerOptional.get()).stream()
                .map(DeviceMapper::toDeviceDetailsDTO)
                .toList();
    }

    public DeviceDetailsDTO getDeviceById(Long id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceMapper.toDeviceDetailsDTO(deviceOptional.get());
    }

    @Transactional
    public DeviceDetailsDTO createDevice(DeviceOperationDTO deviceOperationDTO) {
        Device device = DeviceMapper.toDeviceEntity(deviceOperationDTO);

        User owner = null;
        if (deviceOperationDTO.getUserId() != null) {
            Optional<User> ownerOptional = userRepository.findById(deviceOperationDTO.getUserId());

            if (ownerOptional.isEmpty()) {
                LOGGER.error("User id {} was not found in db", deviceOperationDTO.getUserId());
                throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + deviceOperationDTO.getUserId());
            }

            owner = ownerOptional.get();
        }

        device.setOwner(owner);
        device = deviceRepository.save(device);

        try {
            String payloadJson = objectMapper.writeValueAsString(new DeviceEvent(device.getId(), device.getMaxConsumption()));
            publishSyncEvent("DEVICE_CREATED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send create device sync message {}", device, e);
        }

        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return DeviceMapper.toDeviceDetailsDTO(device);
    }

    @Transactional
    public DeviceDetailsDTO updateDevice(Long id, DeviceOperationDTO deviceOperationDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db, updating it is not possible", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        User owner = null;
        if (deviceOperationDTO.getUserId() != null) {
            Optional<User> ownerOptional = userRepository.findById(deviceOperationDTO.getUserId());

            if (ownerOptional.isEmpty()) {
                LOGGER.error("User id {} was not found in db", deviceOperationDTO.getUserId());
                throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + deviceOperationDTO.getUserId());
            }

            owner = ownerOptional.get();
        }

        Device existingDevice = deviceOptional.get();
        existingDevice.setName(deviceOperationDTO.getName());
        existingDevice.setMaxConsumption(deviceOperationDTO.getMaxConsumption());
        existingDevice.setManufacturer(deviceOperationDTO.getManufacturer());
        existingDevice.setModel(deviceOperationDTO.getModel());
        existingDevice.setDescription(deviceOperationDTO.getDescription());
        existingDevice.setOwner(owner);

        Device updatedDevice = deviceRepository.save(existingDevice);

        try {
            String payloadJson = objectMapper.writeValueAsString(new DeviceEvent(updatedDevice.getId(), updatedDevice.getMaxConsumption()));
            publishSyncEvent("DEVICE_UPDATED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send update device sync message {}", updatedDevice, e);
        }

        LOGGER.debug("Device with id {} was updated in db", updatedDevice.getId());
        return DeviceMapper.toDeviceDetailsDTO(updatedDevice);
    }

    @Transactional
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);

        try {
            String payloadJson = objectMapper.writeValueAsString(new DeviceEvent(id, null));
            publishSyncEvent("DEVICE_DELETED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send delete device sync message {}", id, e);
        }

        LOGGER.debug("Device with id {} was deleted from the db", id);
    }

    @Transactional
    public void removeUserFromDevices(Long id) {
        Optional<User> ownerOptional = userRepository.findById(id);
        if (ownerOptional.isEmpty()) {
            LOGGER.error("User id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        List<Device> devicesOfUser = deviceRepository.findAllByOwner(ownerOptional.get());
        for (Device device : devicesOfUser) {
            device.setOwner(null);
            deviceRepository.save(device);
        }

        userRepository.deleteById(id);

        LOGGER.debug("Removed user id {} from every device owned by them", id);
    }

    public void manageOverconsumption(OverconsumptionEvent overconsumptionEvent) {
        Optional<Device> deviceOptional = deviceRepository.findById(overconsumptionEvent.getDeviceId());
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", overconsumptionEvent.getDeviceId());
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + overconsumptionEvent.getDeviceId());
        }

        Device device = deviceOptional.get();

        if (device.getOwner() != null) {
            Long userId = device.getOwner().getId();

            OverconsumptionDetailedEvent event = OverconsumptionDetailedEvent.builder()
                    .deviceId(device.getId())
                    .deviceName(device.getName())
                    .userId(userId)
                    .day(overconsumptionEvent.getDay())
                    .hour(overconsumptionEvent.getHour())
                    .currentConsumption(overconsumptionEvent.getCurrentConsumption())
                    .maxConsumption(device.getMaxConsumption())
                    .measurementCount(overconsumptionEvent.getMeasurementCount())
                    .build();

            try {
                String payloadJson = objectMapper.writeValueAsString(event);
                publishOverconsumptionEvent("OVERCONSUMPTION_DETAILED", payloadJson);
            } catch (Exception e) {
                LOGGER.error("Error while trying to send detailed device overconsumption sync message", e);
            }
        }
    }

}
