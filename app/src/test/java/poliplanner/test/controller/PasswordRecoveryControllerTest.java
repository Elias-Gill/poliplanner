package poliplanner.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;

import poliplanner.controller.LoginController;
import poliplanner.services.PasswordRecoveryService;

@WebMvcTest(LoginController.class) // O el controller de recovery
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
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/login"))
                .andExpect(model().attributeExists("success"));
    }

    @Test
    void postResetPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/recovery/elias/token123")
                .param("newPassword", "pass1")
                .param("confirmPassword", "pass2"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/reset_password_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postResetPasswordInvalidToken() throws Exception {
        when(recoveryService.resetPassword("elias", "token123", "newPass123")).thenReturn(false);

        mockMvc.perform(post("/user/recovery/elias/token123")
                .param("newPassword", "newPass123")
                .param("confirmPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/auth/reset_password_form"))
                .andExpect(model().attributeExists("error"));
    }
}
