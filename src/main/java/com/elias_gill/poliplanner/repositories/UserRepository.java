package com.elias_gill.poliplanner.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elias_gill.poliplanner.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);
}
