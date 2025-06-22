package com.elias_gill.poliplanner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.services.UserService;

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
            @ModelAttribute("user") User user,
            RedirectAttributes redirectAttributes) {

        try {
            userService.registerUser(user.getUsername(), user.getPassword());
        } catch (InternalServerErrorException e) {
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Internal server error. Please try again latter");
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", e.getMessage());
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Registro exitoso! Por favor inicia sesión.");
        return "redirect:/login";
    }
}
