package com.elias_gill.poliplanner.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.UserService;

@Controller
public class ScheduleController {

    private final UserService userService;
    private final ScheduleService scheduleService;

    public ScheduleController(UserService userService, ScheduleService scheduleService) {
        this.userService = userService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        List<Schedule> schedules = scheduleService.findByUserName(userName);

        model.addAttribute("schedules", schedules);

        return "pages/home";
    }
}
