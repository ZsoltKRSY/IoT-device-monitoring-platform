package sd.devices.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverconsumptionEvent implements Serializable {
    private Long deviceId;
    private LocalDate day;
    private Integer hour;
    private Float currentConsumption;
    private Integer measurementCount;
}