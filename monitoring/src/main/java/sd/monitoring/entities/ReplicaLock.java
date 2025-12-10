package sd.monitoring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "replica_lock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplicaLock {

    @Id
    @Column(name = "replica_id")
    private Integer replicaId;

    @Column(name = "locked_by", nullable = false)
    private String lockedBy;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

}
