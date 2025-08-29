package poliplanner.services;

import poliplanner.models.User;
import poliplanner.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Nuevo servicio solo para seguridad
@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // Metodo requerido por UserDetailsService (para Spring Security)
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        User user = userRepository
                .findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail)) // intenta con email si no encontró por username
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o email no encontrado"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
