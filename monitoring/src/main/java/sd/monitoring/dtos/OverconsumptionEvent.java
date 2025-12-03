package sd.monitoring.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverconsumptionEvent {
    private Long deviceId;
    private LocalDate day;
    private Integer hour;
    private Float currentConsumption;
    private Float maxConsumption;
    private Integer measurementCount;
}
