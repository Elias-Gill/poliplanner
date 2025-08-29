package poliplanner.test.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import poliplanner.controller.PasswordRecoveryController;
import poliplanner.services.exception.InvalidTokenException;
import poliplanner.services.exception.UserNotFoundException;
import poliplanner.services.PasswordRecoveryService;

@WebMvcTest(PasswordRecoveryController.class)
class PasswordRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordRecoveryService recoveryService;

    @Test
    void getRecoveryFormShowsPage() throws Exception {
        mockMvc.perform(get("/user/recovery"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/recovery"));
    }

    @Test
    void postRecoveryRequestSuccess() throws Exception {
        doNothing().when(recoveryService).startRecoveryProcess("elias@example.com");

        mockMvc.perform(post("/user/recovery")
                .with(csrf())
                .param("email", "elias@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recovery"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void postRecoveryRequestUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Usuario no encontrado"))
                .when(recoveryService).startRecoveryProcess("unknown@example.com");

        mockMvc.perform(post("/user/recovery")
                .with(csrf())
                .param("email", "unknown@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recovery"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void getResetFormShowsPage() throws Exception {
        doNothing().when(recoveryService).validateToken("elias", "token123");

        mockMvc.perform(get("/user/recovery/elias/token123"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/reset_password_form"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("token"));
    }

    @Test
    void getResetFormInvalidToken() throws Exception {
        doThrow(new InvalidTokenException("Token inválido"))
                .when(recoveryService).validateToken("elias", "invalidToken");

        mockMvc.perform(get("/user/recovery/elias/invalidToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recovery"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void postResetPasswordSuccess() throws Exception {
        doNothing().when(recoveryService).resetPassword("elias", "token123", "newPass123");

        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void postResetPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "pass1")
                .param("confirmPassword", "pass2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recovery/elias/token123"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void postResetPasswordInvalidToken() throws Exception {
        doThrow(new InvalidTokenException("Token inválido"))
                .when(recoveryService).resetPassword("elias", "token123", "newPass123");

        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recovery"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void postResetPasswordUnexpectedError() throws Exception {
        doThrow(new RuntimeException("Error inesperado"))
                .when(recoveryService).resetPassword("elias", "token123", "newPass123");

        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("error"));
    }

    @TestConfiguration
    public static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable()); // CSRF deshabilitado para tests
            return http.build();
        }
    }
}
