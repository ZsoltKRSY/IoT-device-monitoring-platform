package sd.monitoring.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "consumption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Device device;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Column(name = "hour", nullable = false)
    private Integer hour;

    @Column(name = "total_consumption", nullable = false)
    private Double totalConsumption;

    @Column(name = "measurement_count")
    private Integer measurementCount;
}