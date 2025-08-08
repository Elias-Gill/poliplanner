package com.elias_gill.poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // NOTE: la logica de redireccion es manejada automaticamente por Spring
        return "pages/auth/login";
    }
}
