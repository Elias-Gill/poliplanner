package poliplanner.test.controller;

import static org.mockito.Mockito.when;
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
import poliplanner.services.PasswordRecoveryService;

@WebMvcTest(PasswordRecoveryController.class)
class PasswordRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordRecoveryService recoveryService;

    @Test
    void getResetFormShowsPage() throws Exception {
        mockMvc.perform(get("/user/recovery/elias/token123"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/reset_password_form"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("token"));
    }

    @Test
    void postResetPasswordSuccess() throws Exception {
        when(recoveryService.resetPassword("elias", "token123", "newPass123")).thenReturn(true);

        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("pages/auth/login"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void postResetPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "pass1")
                .param("confirmPassword", "pass2"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/reset_password_form"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("token"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    void postResetPasswordInvalidToken() throws Exception {
        when(recoveryService.resetPassword("elias", "token123", "newPass123")).thenReturn(false);

        mockMvc.perform(post("/user/recovery/elias/token123")
                .with(csrf())
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("pages/auth/login"))
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
