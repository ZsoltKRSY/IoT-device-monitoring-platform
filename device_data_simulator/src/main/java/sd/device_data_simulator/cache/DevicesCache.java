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

    public List<Long> get10RandomDeviceIds() {
        Random random = new Random();
        List<Long> deviceIds = new ArrayList<>(this.deviceIds);
        List<Long> ids = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            ids.add(deviceIds.get(random.nextInt(deviceIds.size())));
        }

        return ids;
    }
}