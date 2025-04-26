package com.elias_gill.poliplanner.controller;

import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "pages/signup";
    }

    // Procesa el formulario de registro
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Registrar el nuevo usuario
            userService.registerUser(username, password);

            // 2. Redirigir con mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage", "Registro exitoso! Por favor inicia sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            // 3. Manejar errores (usuario ya existe, etc.)
            redirectAttributes.addFlashAttribute("errorMessage", "Error en el registro: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
