package sd.monitoring.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionDTO {
    private Long id;
    private Long deviceId;
    private LocalDate day;
    private Integer hour;
    private Double totalConsumption;
    private Integer measurementCount;
}
