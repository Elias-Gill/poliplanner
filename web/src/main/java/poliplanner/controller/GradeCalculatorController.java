package poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calculator")
public class GradeCalculatorController {
    @GetMapping
    public String calculadora() {
        return "pages/grades/calculator";
    }
}
