package poliplanner.test.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import poliplanner.exception.BadArgumentsException;
import poliplanner.exception.UserNameAlreadyExistsException;
import poliplanner.models.User;
import poliplanner.repositories.UserRepository;
import poliplanner.services.UserService;

@SpringBootTest
@Transactional
@Rollback
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterUser_success() throws Exception {
        String username = "test-user";
        String rawPassword = "password123";

        // Limpiar si ya existe (por si se repite el test)
        userRepository.findByUsername(username).ifPresent(user -> userRepository.delete(user));

        // Throws si es que se pasa un nombre invalido
        assertThrows(BadArgumentsException.class,
                () -> userService.registerUser(username.toUpperCase(), rawPassword));

        // Creacion valida
        User createdUser = userService.registerUser(username, rawPassword);
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertTrue(passwordEncoder.matches(rawPassword, createdUser.getPassword()));

        // Verificar que esté en BD
        assertTrue(userRepository.findByUsername(username).isPresent());
    }

    @Test
    void testRegisterUser_existingUsername() throws Exception {
        String username = "existinguser";
        String rawPassword = "password123";

        // Crear usuario previo para forzar excepción
        User user = new User();
        user.setUsername(username.toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        UserNameAlreadyExistsException ex = assertThrows(
                UserNameAlreadyExistsException.class,
                () -> userService.registerUser(username, rawPassword));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    @Test
    void testRegisterUser_badArguments() {
        assertThrows(BadArgumentsException.class, () -> userService.registerUser(null, "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("validuser", null));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("", "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("invalid user!", "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("validuser", "123")); // password corto
    }
}
