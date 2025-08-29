package poliplanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poliplanner.services.PasswordRecoveryService;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService recoveryService;

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
}
