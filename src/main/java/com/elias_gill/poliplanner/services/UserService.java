package com.elias_gill.poliplanner.services;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

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

        if (username == null) {
            throw new BadArgumentsException("El nombre de usuario no puede estar vacio.");
        }

        if (rawPassword == null) {
            throw new BadArgumentsException("La contraseña no puede estar vacia.");
        }

        // Los nombres de usuario se guardan solo en lowercase para ser mas estandar
        username = username.toLowerCase().trim();

        validateUserName(username);
        validateRawPassword(rawPassword);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNameAlreadyExistsException("El nombre de usuario '" + username + "' ya está en uso.");
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

    static void validateUserName(String username) throws BadArgumentsException {
        if (username.isEmpty()) {
            throw new BadArgumentsException("El nombre de usuario no puede estar vacio.");
        }

        if (!VALID_USERNAME_PATTERN.matcher(username).matches()) {
            throw new BadArgumentsException("El nombre de usuario solo puede contener letras, números, '-' o '_'.");
        }
    }

    static void validateRawPassword(String rawPassword) throws BadArgumentsException {
        if (rawPassword.length() < 6) {
            throw new BadArgumentsException("La contraseña debe contener al menos 6 caracteres.");
        }
    }
}
