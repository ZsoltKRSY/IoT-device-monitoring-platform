package sd.monitoring.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceEvent implements Serializable {
    private Long deviceId;
    private Float maxConsumption;
}
