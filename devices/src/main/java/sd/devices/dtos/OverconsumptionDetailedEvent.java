package sd.devices.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverconsumptionDetailedEvent implements Serializable {
    private Long deviceId;
    private String deviceName;
    private Long userId;
    private LocalDate day;
    private Integer hour;
    private Float currentConsumption;
    private Float maxConsumption;
    private Integer measurementCount;
}