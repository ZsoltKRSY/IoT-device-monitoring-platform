package sd.monitoring.services;

import jakarta.annotation.PreDestroy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sd.monitoring.helper.ReplicaLockHelper;
import sd.monitoring.reporitories.ReplicaLockRepository;

import java.net.InetAddress;

@Service
public class ReplicaIdService {

    private final ReplicaLockHelper replicaLockHelper;
    private final ReplicaLockRepository replicaLockRepository;

    public ReplicaIdService(ReplicaLockHelper replicaLockHelper, ReplicaLockRepository replicaLockRepository) {
        this.replicaLockHelper = replicaLockHelper;
        this.replicaLockRepository = replicaLockRepository;
    }

    public int findAndClaimId() {
        int replicaId = 1;
        final String hostname = getHostname();

        while (true) {
            try {
                replicaLockHelper.attemptClaim(replicaId, hostname);

                System.out.println("Successfully claimed Replica ID: " + replicaId);

                return replicaId;

            } catch (DataIntegrityViolationException e) {
                System.out.println("ID " + replicaId + " is already claimed. Trying next.");
                replicaId++;

            } catch (Exception e) {
                throw new RuntimeException("Failed during ID assignment.", e);
            }
        }
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @PreDestroy
    @Transactional
    public void releaseReplicaLocks() {
        replicaLockRepository.deleteAll();

        System.out.println("Successfully wiped claimed Replica IDs.");
    }
}