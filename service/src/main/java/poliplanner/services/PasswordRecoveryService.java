package poliplanner.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import poliplanner.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    final private UserRepository userRepository;
    final private EmailService emailService;
    final private BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    // FIX: que no explote si es que no se configuro
    @Value("${app.domain.url}")
    private String domainUrl;

    @Transactional
    public boolean startRecoveryProcess(String email) {
        return userRepository.findByEmail(email).map(user -> {
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

            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean resetPassword(String username, String token, String newPassword) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    // Verificar si el token ya fue usado
                    if (user.isRecoveryTokenUsed()) {
                        return false;
                    }

                    // Verificar expiración
                    if (user.getRecoveryTokenExpiration().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    // Comparar token con hash
                    if (!pwdEncoder.matches(token, user.getRecoveryTokenHash())) {
                        return false;
                    }

                    // Actualizar contraseña y marcar token como usado
                    user.setPassword(pwdEncoder.encode(newPassword));
                    user.setRecoveryTokenUsed(true);
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }
}
