package sd.device_data_simulator.cache;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DevicesCache {

    private final Set<Long> deviceIds = new HashSet<>();

    public synchronized void updateDevices(List<Long> ids) {
        deviceIds.clear();
        deviceIds.addAll(ids);
    }

    public List<Long> getDeviceIds() {
        return new ArrayList<>(deviceIds);
    }
}