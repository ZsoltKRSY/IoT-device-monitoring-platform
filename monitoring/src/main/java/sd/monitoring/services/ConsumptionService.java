package sd.monitoring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sd.monitoring.dtos.ConsumptionDTO;
import sd.monitoring.dtos.MeasurementEvent;
import sd.monitoring.entities.Consumption;
import sd.monitoring.entities.Device;
import sd.monitoring.handlers.models.ResourceNotFoundException;
import sd.monitoring.reporitories.ConsumptionRepository;
import sd.monitoring.reporitories.DeviceReporitory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumptionService.class);

    private final ConsumptionRepository consumptionRepository;
    private final DeviceReporitory deviceReporitory;

    public ConsumptionService(ConsumptionRepository consumptionRepository, DeviceReporitory deviceReporitory) {
        this.consumptionRepository = consumptionRepository;
        this.deviceReporitory = deviceReporitory;
    }

    @Transactional
    public List<ConsumptionDTO> getDeviceConsumptionForDay(Long deviceId, LocalDate day) {
        Optional<Device> deviceOptional = deviceReporitory.findById(deviceId);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }
        Device device = deviceOptional.get();

        List<Consumption> entries = consumptionRepository.findByDeviceAndDay(device, day);

        Map<Integer, Consumption> byHour = entries.stream().collect(Collectors.toMap(Consumption::getHour, c -> c));

        List<ConsumptionDTO> result = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            if (byHour.containsKey(hour)) {
                Consumption c = byHour.get(hour);
                result.add(
                        ConsumptionDTO.builder()
                                .id(c.getId())
                                .deviceId(device.getId())
                                .day(c.getDay())
                                .hour(hour)
                                .totalConsumption(c.getTotalConsumption())
                                .measurementCount(c.getMeasurementCount())
                                .build());
            } else {
                result.add(
                        ConsumptionDTO.builder()
                                .id(null)
                                .deviceId(device.getId())
                                .day(day)
                                .hour(hour)
                                .totalConsumption(0.0)
                                .measurementCount(0)
                                .build());
            }
        }

        return result;
    }

    @Transactional
    public void processMeasurement(MeasurementEvent measurementEvent) {
        Optional<Device> deviceOptional = deviceReporitory.findById(measurementEvent.getDeviceId());
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device id {} was not found in db", measurementEvent.getDeviceId());
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + measurementEvent.getDeviceId());
        }
        Device device = deviceOptional.get();

        Instant instant = Instant.ofEpochMilli(measurementEvent.getTimestamp());
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());

        LocalDate day = zdt.toLocalDate();
        int hour = zdt.getHour();

        Optional<Consumption> entryOptional = consumptionRepository.findByDeviceAndDayAndHour(device, day, hour);

        Consumption entry = entryOptional.orElseGet(() ->
                Consumption.builder()
                        .device(device)
                        .day(day)
                        .hour(hour)
                        .totalConsumption(0.0)
                        .measurementCount(0)
                        .build()
        );

        entry.setTotalConsumption(entry.getTotalConsumption() + measurementEvent.getValue());
        entry.setMeasurementCount(entry.getMeasurementCount() + 1);

        consumptionRepository.save(entry);
    }

}