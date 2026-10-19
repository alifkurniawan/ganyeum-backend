package id.reshito.backend.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUid(String uid);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findAllByPhoneNumber(String phoneNumber);
    List<User> findAllByNameIgnoreCase(String name);
}
