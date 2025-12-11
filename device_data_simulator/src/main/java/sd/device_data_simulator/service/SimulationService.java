package sd.device_data_simulator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import sd.device_data_simulator.cache.DevicesCache;
import sd.device_data_simulator.dtos.MeasurementEvent;
import sd.device_data_simulator.producer.MeasurementProducer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SimulationService {

    private final DevicesCache devicesCache;
    private final MeasurementProducer producer;

    private long currentTimestamp;
    private final Map<Long, Double> baseLoadMap = new HashMap<>();

    private final Random random = new Random();

    private static final long TEN_MINUTES_MS = 600_000L; // 10 minutes in milliseconds


    public SimulationService(DevicesCache devicesCache, MeasurementProducer producer) {
        this.devicesCache = devicesCache;
        this.producer = producer;

        this.currentTimestamp = System.currentTimeMillis();
    }

    public void generateMeasurements(int count) throws IllegalStateException {
        List<Long> deviceIds = devicesCache.get10RandomDeviceIds();

        if (deviceIds.isEmpty()) {
            throw new IllegalStateException("No device IDs loaded yet.");
        }

        for (int i = 0; i < count; i++) {
            for(Long deviceId : deviceIds) {
                double value = generateConsumptionValue(deviceId, currentTimestamp);

                MeasurementEvent measurementEvent = new MeasurementEvent(
                        currentTimestamp,
                        deviceId,
                        value
                );

                producer.publishMeasurementEvent(measurementEvent);
            }

            currentTimestamp += TEN_MINUTES_MS;
        }
    }

    private double generateConsumptionValue(Long deviceId, long timestamp) {
        double baseLoad = baseLoadMap.computeIfAbsent(
                deviceId,
                id -> 0.01 + (0.3 * random.nextDouble())
        );

        LocalDateTime time = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        int hour = time.getHour();

        double timeFactor = getTimeOfDayFactor(hour);
        double fluctuation = getFluctuation();

        return Math.max(0.01, baseLoad * timeFactor * fluctuation);
    }

    private double getTimeOfDayFactor(int hour) {
        return switch (hour) {
            case 0,1,2,3,4,5 -> 0.5;   // deep night
            case 6 -> 0.6;             // dawn
            case 7,8,9 -> 0.8;         // morning
            case 10,11 -> 1.0;        // late morning
            case 12,13,14,15,16,17 -> 1.1; // day stable
            case 18,19 -> 1.4;        // evening peak
            case 20,21 -> 1.3;
            case 22 -> 1.1;
            case 23 -> 0.8;           // late evening
            default -> 1.0;
        };
    }

    private double getFluctuation() {
        double delta = (random.nextDouble() * 0.20) - 0.10;
        return 1.0 + delta;
    }

}