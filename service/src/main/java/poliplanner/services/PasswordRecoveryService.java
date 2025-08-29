package poliplanner.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import poliplanner.models.User;
import poliplanner.repositories.UserRepository;
import poliplanner.services.exception.InvalidTokenException;
import poliplanner.services.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    final private UserRepository userRepository;
    final private EmailService emailService;
    final private BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @Value("${app.domain.url}")
    private String domainUrl;

    @Transactional
    public void startRecoveryProcess(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No se encontró ninguna cuenta con el correo: " + email));

        String token = UUID.randomUUID().toString();
        String hashedToken = pwdEncoder.encode(token);
        LocalDateTime expiration = LocalDateTime.now().plusHours(1);

        user.setRecoveryTokenHash(hashedToken);
        user.setRecoveryTokenExpiration(expiration);
        user.setRecoveryTokenUsed(false);
        userRepository.save(user);

        String recoveryLink = domainUrl + "/user/recovery/" + user.getUsername() + "/" + token;
        emailService.sendSimpleMessage(
                email,
                "Recuperación de contraseña",
                "Haz clic en el siguiente enlace para recuperar tu contraseña: \n" + recoveryLink);
    }

    public void validateToken(String username, String token) throws InvalidTokenException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        validateTokenForUser(user, token);
    }

    @Transactional
    public void resetPassword(String username, String token, String newPassword) throws InvalidTokenException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        validateTokenForUser(user, token);

        // Actualizar contraseña y marcar token como usado
        user.setPassword(pwdEncoder.encode(newPassword));
        user.setRecoveryTokenUsed(true);
        userRepository.save(user);
    }

    private void validateTokenForUser(User user, String token) throws InvalidTokenException {
        // Verificar si el token ya fue usado
        if (user.isRecoveryTokenUsed()) {
            throw new InvalidTokenException("El token de recuperación ya fue utilizado");
        }

        // Verificar expiración
        if (user.getRecoveryTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El token de recuperación ha expirado");
        }

        // Comparar token con hash
        if (!pwdEncoder.matches(token, user.getRecoveryTokenHash())) {
            throw new InvalidTokenException("Token inválido");
        }
    }
}
