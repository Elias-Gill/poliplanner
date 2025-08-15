package poliplanner.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import poliplanner.exception.BadArgumentsException;

public class UserServiceTest {
    @Test
    void testPasswordValidation() throws Exception {
        // Comprobando que password demasiado corto falla:
        BadArgumentsException pwEx = assertThrows(BadArgumentsException.class,
                () -> UserService.validateRawPassword("123"));
        assertTrue(pwEx.getMessage().contains("contraseña"));
    }

    @Test
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

}
