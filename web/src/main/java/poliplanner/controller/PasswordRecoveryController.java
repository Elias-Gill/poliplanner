package poliplanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poliplanner.services.PasswordRecoveryService;
import poliplanner.services.exception.InvalidTokenException;
import poliplanner.services.exception.UserNotFoundException;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService recoveryService;

    // Formulario para enviar el email de recuperacion de password
    @GetMapping("/user/recovery")
    public String showRecoveryForm() {
        return "pages/auth/recovery";
    }

    @PostMapping("/user/recovery")
    public String processRecoveryRequest(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        try {
            recoveryService.startRecoveryProcess(email);
            redirectAttributes.addFlashAttribute("success",
                    "Se ha enviado un correo con instrucciones para recuperar tu contraseña.");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se encontró ninguna cuenta con ese correo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado. Por favor, intente nuevamente.");
        }

        return "redirect:/user/recovery";
    }

    // Formulario de reseteo de password
    @GetMapping("/user/recovery/{username}/{token}")
    public String showResetForm(
            @PathVariable String username,
            @PathVariable String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            recoveryService.validateToken(username, token);
            model.addAttribute("token", token);
            model.addAttribute("username", username);
            return "pages/auth/reset_password_form";

        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Token inválido o expirado. Por favor, solicite un nuevo enlace de recuperación.");
            return "redirect:/user/recovery";
        }
    }

    @PostMapping("/user/recovery/{username}/{token}")
    public String processReset(
            @PathVariable String username,
            @PathVariable String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Validar coincidencia de contraseñas
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/user/recovery/" + username + "/" + token;
            }

            recoveryService.resetPassword(username, token, newPassword);
            redirectAttributes.addFlashAttribute("success",
                    "Contraseña cambiada correctamente. Ahora podés iniciar sesión.");

        } catch (InvalidTokenException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Token inválido o expirado. Por favor, solicite un nuevo enlace de recuperación.");
            return "redirect:/user/recovery";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar la contraseña. Por favor, intente nuevamente.");
        }

        return "redirect:/login";
    }
}
