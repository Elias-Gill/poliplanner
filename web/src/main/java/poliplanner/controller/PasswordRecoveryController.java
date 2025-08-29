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
    public String processRecoveryRequest(@RequestParam("email") String email, Model model) {
        boolean success = recoveryService.startRecoveryProcess(email);

        if (success) {
            model.addAttribute("success", "Se ha enviado un correo con instrucciones para recuperar tu contraseña.");
        } else {
            model.addAttribute("error", "No se encontró ninguna cuenta con ese correo.");
        }

        return "pages/auth/recovery";
    }

    // Formulario de reseteo de password
    @GetMapping("/user/recovery/{username}/{token}")
    public String showResetForm(
            @PathVariable String username,
            @PathVariable String token,
            Model model) {

        model.addAttribute("token", token);
        model.addAttribute("username", username);
        return "pages/auth/reset_password_form";
    }

    @PostMapping("/user/recovery/{username}/{token}")
    public String processReset(
            @PathVariable String username,
            @PathVariable String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            model.addAttribute("username", username);
            return "pages/auth/reset_password_form";
        }

        boolean success = recoveryService.resetPassword(username, token, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success",
                    "Contraseña cambiada correctamente. Ahora podés iniciar sesión.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Token inválido o expirado.");
        }

        return "redirect:pages/auth/login";
    }
}
