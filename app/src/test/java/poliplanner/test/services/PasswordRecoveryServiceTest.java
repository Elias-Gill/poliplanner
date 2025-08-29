package poliplanner.test.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import poliplanner.models.User;
import poliplanner.repositories.UserRepository;
import poliplanner.services.PasswordRecoveryService;
import poliplanner.services.exception.InvalidTokenException;
import poliplanner.services.exception.UserNotFoundException;

class PasswordRecoveryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordRecoveryService recoveryService;

    private BCryptPasswordEncoder encoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        encoder = new BCryptPasswordEncoder();

        testUser = new User();
        testUser.setUsername("elias");
        testUser.setEmail("elias@example.com");
        testUser.setRecoveryTokenHash(encoder.encode("token123"));
        testUser.setRecoveryTokenExpiration(LocalDateTime.now().plusHours(1));
        testUser.setRecoveryTokenUsed(false);
    }

    @Test
    void testStartRecoveryProcessSuccess() throws UserNotFoundException {
        when(userRepository.findByEmail("elias@example.com")).thenReturn(Optional.of(testUser));

        recoveryService.startRecoveryProcess("elias@example.com");

        verify(userRepository).save(any(User.class));
        // Verificar que se generó un nuevo token
        assertTrue(testUser.getRecoveryTokenHash() != null);
    }

    @Test
    void testStartRecoveryProcessUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            recoveryService.startRecoveryProcess("unknown@example.com");
        });
    }

    @Test
    void testValidateTokenSuccess() throws InvalidTokenException {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        recoveryService.validateToken("elias", "token123");
    }

    @Test
    void testValidateTokenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.validateToken("unknown", "token123");
        });
    }

    @Test
    void testValidateTokenExpired() {
        testUser.setRecoveryTokenExpiration(LocalDateTime.now().minusMinutes(5));
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.validateToken("elias", "token123");
        });
    }

    @Test
    void testValidateTokenInvalidToken() {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.validateToken("elias", "wrongtoken");
        });
    }

    @Test
    void testValidateTokenAlreadyUsed() {
        testUser.setRecoveryTokenUsed(true);
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.validateToken("elias", "token123");
        });
    }

    @Test
    void testResetPasswordSuccess() throws InvalidTokenException {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        recoveryService.resetPassword("elias", "token123", "newPass123");

        assertTrue(encoder.matches("newPass123", testUser.getPassword()));
        assertTrue(testUser.isRecoveryTokenUsed());
        verify(userRepository).save(testUser);
    }

    @Test
    void testResetPasswordExpiredToken() {
        testUser.setRecoveryTokenExpiration(LocalDateTime.now().minusMinutes(5));
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.resetPassword("elias", "token123", "newPass123");
        });
    }

    @Test
    void testResetPasswordInvalidToken() {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.resetPassword("elias", "wrongtoken", "newPass123");
        });
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.resetPassword("unknown", "token123", "newPass123");
        });
    }

    @Test
    void testResetPasswordTokenAlreadyUsed() {
        testUser.setRecoveryTokenUsed(true);
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        assertThrows(InvalidTokenException.class, () -> {
            recoveryService.resetPassword("elias", "token123", "newPass123");
        });
    }
}
