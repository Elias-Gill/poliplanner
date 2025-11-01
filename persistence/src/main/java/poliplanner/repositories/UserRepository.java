package poliplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import poliplanner.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);
}
