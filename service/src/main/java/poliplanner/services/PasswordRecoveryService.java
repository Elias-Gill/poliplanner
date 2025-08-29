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

            String recoveryLink = domainUrl + "/user/recovery/" + token;
            emailService.sendSimpleMessage(
                    email,
                    "Recuperación de contraseña",
                    "Haz clic en el siguiente enlace para recuperar tu contraseña: \n" + recoveryLink);

            return true;
        }).orElse(false);
    }
}
