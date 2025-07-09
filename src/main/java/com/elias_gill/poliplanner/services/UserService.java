package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.UserRepository;

import java.util.Optional;

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

    public Optional<User> findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public User registerUser(String username, String rawPassword)
            throws UserNameAlreadyExistsException,
            BadArgumentsException,
            InternalServerErrorException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNameAlreadyExistsException(
                    "Username '" + username + "' is already taken.");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new BadArgumentsException("Username must not be empty.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new BadArgumentsException("Password must be at least 6 characters long.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error while registering a new user: ", e);
        }
    }
}
