package com.elias_gill.poliplanner.controller;

import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.exception.UserNameAlreadyExistsException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "pages/register";
    }

    // Procesa el formulario de registro
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {

        try {
            userService.registerUser(user.getUsername(), user.getPassword());
        } catch (UserNameAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso.");
            return "redirect:/register";
        } catch (InternalServerErrorException e) {
            logger.error("Error interno en registro de usuario: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Internal server error. Please try again later");
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "¡Registro exitoso! Por favor, inicia sesión.");
        return "redirect:/login";
    }
}
