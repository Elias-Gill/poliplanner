package com.elias_gill.poliplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elias_gill.poliplanner.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
