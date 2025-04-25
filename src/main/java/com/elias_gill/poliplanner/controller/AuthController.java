package com.elias_gill.poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Solo muestra la plantilla, sin lógica de redirección
    }
}
