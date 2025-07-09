package com.elias_gill.poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/calculator")
public class GradeCalculatorController {
    @GetMapping
    public String calculadora() {
        return "pages/calculadora";
    }
}
