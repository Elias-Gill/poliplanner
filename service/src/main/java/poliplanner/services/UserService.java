package poliplanner.services;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import poliplanner.models.User;
import poliplanner.repositories.UserRepository;
import poliplanner.services.exception.InternalServerErrorException;
import poliplanner.services.exception.ServiceBadArgumentsException;
import poliplanner.services.exception.UserNameAlreadyExistsException;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-z0-9_-]+$");

    public Optional<User> findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public User registerUser(String username, String rawPassword)
            throws UserNameAlreadyExistsException,
                    ServiceBadArgumentsException,
                    InternalServerErrorException {

        if (username == null) {
            throw new ServiceBadArgumentsException("El nombre de usuario no puede estar vacio.");
        }

        if (rawPassword == null) {
            throw new ServiceBadArgumentsException("La contraseña no puede estar vacia.");
        }

        // Los nombres de usuario se guardan solo en lowercase para ser mas estandar
        username = validateAndCleanUserName(username);
        validateRawPassword(rawPassword);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNameAlreadyExistsException(
                    "El nombre de usuario '" + username + "' ya está en uso.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error registrando usuario: ", e);
        }
    }

    static String validateAndCleanUserName(String username) throws ServiceBadArgumentsException {
        username = username.trim();
        if (username.isEmpty()) {
            throw new ServiceBadArgumentsException("El nombre de usuario no puede estar vacio.");
        }

        if (!VALID_USERNAME_PATTERN.matcher(username).matches()) {
            throw new ServiceBadArgumentsException(
                    "El nombre de usuario solo puede contener letras minúsculas, números, '-' o"
                        + " '_'.");
        }

        return username.toLowerCase();
    }

    static void validateRawPassword(String rawPassword) throws ServiceBadArgumentsException {
        if (rawPassword.length() < 6) {
            throw new ServiceBadArgumentsException(
                    "La contraseña debe contener al menos 6 caracteres.");
        }
    }
}
