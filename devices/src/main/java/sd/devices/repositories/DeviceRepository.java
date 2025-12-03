package sd.devices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.devices.entities.Device;
import sd.devices.entities.User;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findAllByOwner(User owner);
}
