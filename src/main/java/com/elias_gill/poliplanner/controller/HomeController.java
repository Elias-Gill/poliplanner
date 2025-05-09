package com.elias_gill.poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mensaje", "Hola Mundo desde Spring Boot + Thymeleaf!");
        return "pages/home";
    }
}
