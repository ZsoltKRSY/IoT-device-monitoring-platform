package sd.websocket.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverconsumptionDetailedEvent implements Serializable {
    @Override
    public String toString() {
        return "OverconsumptionDetailedEvent{" +
                "deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", userId=" + userId +
                ", day=" + day +
                ", hour=" + hour +
                ", currentConsumption=" + currentConsumption +
                ", maxConsumption=" + maxConsumption +
                ", measurementCount=" + measurementCount +
                '}';
    }

    private Long deviceId;
    private String deviceName;
    private Long userId;
    private LocalDate day;
    private Integer hour;
    private Float currentConsumption;
    private Float maxConsumption;
    private Integer measurementCount;
}

