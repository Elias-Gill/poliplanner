package com.elias_gill.poliplanner.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.UserRepository;

@SpringBootTest
@Transactional
@Rollback
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Tag("unit")
    void testUserNameValidation() throws Exception {
        // Casos inválidos para validateAndCleanUserName:
        assertThrows(BadArgumentsException.class, () -> UserService.validateAndCleanUserName(""),
                "Debe lanzar excepción si está vacío");
        assertThrows(BadArgumentsException.class, () -> UserService.validateAndCleanUserName("   "),
                "Debe lanzar excepción si solo espacios");
        assertThrows(BadArgumentsException.class, () -> UserService.validateAndCleanUserName("user!name"),
                "No debe permitir caracteres inválidos");
        assertThrows(BadArgumentsException.class, () -> UserService.validateAndCleanUserName("user name"),
                "No debe permitir espacios internos");
        assertThrows(BadArgumentsException.class, () -> UserService.validateAndCleanUserName("USERname"),
                "No debe permitir mayusculas");

        // Casos validos para validateAndCleanUserName:
        assertDoesNotThrow(() -> UserService.validateAndCleanUserName("username"));
        assertDoesNotThrow(() -> UserService.validateAndCleanUserName("user-name_123"));
    }

    @Test
    @Tag("unit")
    void testPasswordValidation() throws Exception {
        // Comprobando que password demasiado corto falla:
        BadArgumentsException pwEx = assertThrows(BadArgumentsException.class,
                () -> UserService.validateRawPassword("123"));
        assertTrue(pwEx.getMessage().contains("contraseña"));
    }

    @Test
    @Tag("integration")
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
    @Tag("integration")
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
    @Tag("integration")
    void testRegisterUser_badArguments() {
        assertThrows(BadArgumentsException.class, () -> userService.registerUser(null, "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("validuser", null));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("", "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("invalid user!", "password123"));
        assertThrows(BadArgumentsException.class, () -> userService.registerUser("validuser", "123")); // password corto
    }
}
