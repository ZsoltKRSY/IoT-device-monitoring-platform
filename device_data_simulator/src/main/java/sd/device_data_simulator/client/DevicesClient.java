package sd.device_data_simulator.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class DevicesClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${devices.service.url}")
    private String devicesServiceUrl;

    public List<Long> fetchDeviceIds() {
        Long[] deviceIds = restTemplate.getForObject(devicesServiceUrl + "/devices/all-ids", Long[].class);

        return deviceIds == null ? null : Arrays.asList(deviceIds);
    }
}
