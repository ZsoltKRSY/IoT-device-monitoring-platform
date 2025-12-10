package sd.monitoring.helper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sd.monitoring.reporitories.ReplicaLockRepository;

import java.time.LocalDateTime;

@Component
public class ReplicaLockHelper {

    private final ReplicaLockRepository replicaLockRepository;

    public ReplicaLockHelper(ReplicaLockRepository replicaLockRepository) {
        this.replicaLockRepository = replicaLockRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void attemptClaim(int replicaId, String hostname) {
        replicaLockRepository.insertAtomicClaim(replicaId, hostname, LocalDateTime.now());
    }
}