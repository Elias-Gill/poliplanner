package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String rawPassword)
            throws UserNameAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNameAlreadyExistsException(
                    "Username '" + username + "' is already taken.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(newUser);
    }

    public void updateUserProfile(Long userId, String newEmail) {
        // Lógica de actualización
    }
}
