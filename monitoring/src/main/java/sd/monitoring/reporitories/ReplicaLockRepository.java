package sd.monitoring.reporitories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sd.monitoring.entities.ReplicaLock;

import java.time.LocalDateTime;

public interface ReplicaLockRepository extends JpaRepository<ReplicaLock, Integer> {

    @Modifying
    @Query(value = "INSERT INTO replica_lock (replica_id, locked_by, claimed_at) VALUES (:id, :lockedBy, :claimedAt)", nativeQuery = true)
    void insertAtomicClaim(@Param("id") int replicaId, @Param("lockedBy") String lockedBy, @Param("claimedAt") LocalDateTime claimedAt);

}
