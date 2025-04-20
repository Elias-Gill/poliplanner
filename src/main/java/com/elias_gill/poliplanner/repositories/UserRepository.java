package com.elias_gill.poliplanner.repositories;

import com.elias_gill.poliplanner.models.User;
import org.springframework.data.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
