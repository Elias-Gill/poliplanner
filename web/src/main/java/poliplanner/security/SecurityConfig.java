package poliplanner.security;

import poliplanner.services.SecurityUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("unused")
    private final SecurityUserDetailsService userDetailsService;

    public SecurityConfig(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Permisos por ruta y método
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/sync").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sync").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sync/ci").permitAll()
                        .requestMatchers(
                                "/calculator",
                                "/guides/**",
                                "/login",
                                "/logout",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/favicon.ico",
                                "/favicon.png",
                                "/robots.txt",
                                "/sitemap.xml")
                        .permitAll()
                        .anyRequest().authenticated())

                // Login por formulario
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())

                // Logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                // CSRF deshabilitado para "/sync"
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/sync")
                        .ignoringRequestMatchers("/sync/ci"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
