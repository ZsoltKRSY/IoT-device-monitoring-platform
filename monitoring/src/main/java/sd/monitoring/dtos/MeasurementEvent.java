package sd.monitoring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementEvent implements Serializable {
    private Long timestamp;
    private Long deviceId;
    private double value;
}