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
import org.springframework.test.context.ActiveProfiles;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.repositories.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
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
        // Casos inválidos para validateUserName:
        assertThrows(BadArgumentsException.class, () -> UserService.validateUserName(""),
                "Debe lanzar excepción si está vacío");
        assertThrows(BadArgumentsException.class, () -> UserService.validateUserName("   "),
                "Debe lanzar excepción si solo espacios");
        assertThrows(BadArgumentsException.class, () -> UserService.validateUserName("user!name"),
                "No debe permitir caracteres inválidos");
        assertThrows(BadArgumentsException.class, () -> UserService.validateUserName("user name"),
                "No debe permitir espacios internos");

        // Casos validos para validateUserName:
        assertDoesNotThrow(() -> UserService.validateUserName("username"));
        assertDoesNotThrow(() -> UserService.validateUserName("user-name_123"));

        // Mayusculas validas en el metodo pero luego se bajan a lowercase
        assertDoesNotThrow(() -> UserService.validateUserName("USERname"));
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
        String username = "TestUser";
        String rawPassword = "password123";

        // Limpiar si ya existe (por si se repite el test)
        userRepository.findByUsername(username.toLowerCase()).ifPresent(user -> userRepository.delete(user));

        User createdUser = userService.registerUser(username, rawPassword);

        assertNotNull(createdUser);
        assertEquals(username.toLowerCase(), createdUser.getUsername());
        assertTrue(passwordEncoder.matches(rawPassword, createdUser.getPassword()));

        // Verificar que esté en BD
        assertTrue(userRepository.findByUsername(username.toLowerCase()).isPresent());
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
