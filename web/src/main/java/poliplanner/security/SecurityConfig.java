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
        http.authorizeHttpRequests(auth -> auth
                // Sync
                .requestMatchers(HttpMethod.GET, "/sync").permitAll()
                .requestMatchers(HttpMethod.POST, "/sync").permitAll()
                .requestMatchers(HttpMethod.POST, "/sync/ci").permitAll()

                // Páginas públicas
                .requestMatchers(HttpMethod.GET, "/calculator", "/guides/**", "/login", "/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/user/recovery/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/user/recovery/**").permitAll()

                // Recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/favicon.png",
                        "/robots.txt", "/sitemap.xml")
                .permitAll()

                // El resto de rutas requiere login
                .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/sync", "/sync/ci"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
