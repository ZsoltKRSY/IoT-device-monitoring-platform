package sd.monitoring.reporitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.monitoring.entities.Device;

public interface DeviceReporitory extends JpaRepository<Device, Long> {
}
