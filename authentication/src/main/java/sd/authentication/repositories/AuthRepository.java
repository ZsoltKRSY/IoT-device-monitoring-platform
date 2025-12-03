package sd.authentication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.authentication.entities.AuthUser;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> getByUsername(String username);
}
