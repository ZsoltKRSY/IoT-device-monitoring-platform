package sd.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.users.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
