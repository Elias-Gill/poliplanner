package com.elias_gill.poliplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guides")
public class AboutHelpController {

    @GetMapping("/index")
    public String indexGuide() {
        return "pages/guides/index";
    }

    @GetMapping("/manual_del_bicho")
    public String manualDelBichoGuide() {
        return "pages/guides/manual_del_bicho";
    }

    @GetMapping("/calculo_notas")
    public String gradeCalculationGuide() {
        return "pages/guides/calculo_notas";
    }

    @GetMapping("/about")
    public String aboutGuide() {
        return "pages/guides/about";
    }
}
