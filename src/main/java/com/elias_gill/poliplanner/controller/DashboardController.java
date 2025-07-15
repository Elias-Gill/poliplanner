package com.elias_gill.poliplanner.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.services.ScheduleService;

@Controller
public class DashboardController {
    private final ScheduleService scheduleService;

    public DashboardController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long id,
            Authentication authentication,
            Model model) {

        String userName = authentication.getName();
        List<Schedule> schedules = scheduleService.findByUserName(userName);

        model.addAttribute("schedules", schedules);

        if (schedules.isEmpty()) {
            return "pages/dashboard/home";
        }

        // Set the latest created schedule as default
        // FUTURE: maybe migrate to a cookie
        model.addAttribute("selectedSchedule", schedules.get(0));
        if (id != null) {
            for (Schedule s : schedules) {
                if (s.getId().equals(id)) {
                    model.addAttribute("selectedSchedule", s);
                    break;
                }
            }
        }

        return "pages/dashboard/home";
    }
}
