package poliplanner.test.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        testUser.setRecoveryTokenHash(encoder.encode("token123"));
        testUser.setRecoveryTokenExpiration(LocalDateTime.now().plusHours(1));
        testUser.setRecoveryTokenUsed(false);
    }

    @Test
    void testResetPasswordSuccess() {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        boolean result = recoveryService.resetPassword("elias", "token123", "newPass123");

        assertTrue(result);
        assertTrue(encoder.matches("newPass123", testUser.getPassword()));
        assertTrue(testUser.isRecoveryTokenUsed());
        verify(userRepository).save(testUser);
    }

    @Test
    void testResetPasswordExpiredToken() {
        testUser.setRecoveryTokenExpiration(LocalDateTime.now().minusMinutes(5));
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        boolean result = recoveryService.resetPassword("elias", "token123", "newPass123");

        assertFalse(result);
        assertFalse(testUser.isRecoveryTokenUsed());
    }

    @Test
    void testResetPasswordInvalidToken() {
        when(userRepository.findByUsername("elias")).thenReturn(Optional.of(testUser));

        boolean result = recoveryService.resetPassword("elias", "wrongtoken", "newPass123");

        assertFalse(result);
        assertFalse(testUser.isRecoveryTokenUsed());
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = recoveryService.resetPassword("unknown", "token123", "newPass123");

        assertFalse(result);
    }
}
