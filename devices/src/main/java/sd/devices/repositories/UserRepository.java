package sd.devices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.devices.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
