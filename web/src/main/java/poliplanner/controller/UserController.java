package poliplanner.controller;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poliplanner.models.User;
import poliplanner.services.UserService;
import poliplanner.services.exception.InternalServerErrorException;
import poliplanner.services.exception.ServiceBadArgumentsException;
import poliplanner.services.exception.UserNameAlreadyExistsException;

@Controller
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "pages/auth/register";
    }

    // Procesa el formulario de registro
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {

        if (!user.getPassword().equals(user.getConfirmedPassword())) {
            model.addAttribute("error", "Las contraseñas deben coincidir");
            return "pages/auth/register";
        }

        try {
            userService.registerUser(user.getUsername(), user.getPassword());
        } catch (ServiceBadArgumentsException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/auth/register";
        } catch (UserNameAlreadyExistsException e) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "pages/auth/register";
        } catch (InternalServerErrorException e) {
            logger.error("Error interno en registro de usuario: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute(
                    "error", "Un error interno acaba de ocurrir. Por favor inténtalo más tarde.");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "¡Registro exitoso! Por favor, inicia sesión.");
        return "redirect:/login";
    }
}
