package sd.monitoring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "max_consumption", columnDefinition = "real", nullable = false)
    private Float maxConsumption;
    
}