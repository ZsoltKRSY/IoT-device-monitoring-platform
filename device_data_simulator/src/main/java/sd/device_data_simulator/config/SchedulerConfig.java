package sd.device_data_simulator.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import sd.device_data_simulator.cache.DevicesCache;
import sd.device_data_simulator.client.DevicesClient;

import java.util.List;

@Configuration
public class SchedulerConfig {

    private final DevicesClient devicesClient;
    private final DevicesCache deviceCache;

    public SchedulerConfig(DevicesClient devicesClient, DevicesCache devicesCache) {
        this.devicesClient = devicesClient;
        this.deviceCache = devicesCache;
    }

    @PostConstruct
    public void init() {
        refreshDevices();
    }

    @Scheduled(fixedRate = 300_000) // 5 minutes
    public void refreshDevices() {
        List<Long> ids = devicesClient.fetchDeviceIds();
        deviceCache.updateDevices(ids);
    }

}