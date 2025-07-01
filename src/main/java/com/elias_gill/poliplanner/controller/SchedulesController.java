package com.elias_gill.poliplanner.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elias_gill.poliplanner.FormsDTO.ScheduleForm;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.SubjectService;
import com.elias_gill.poliplanner.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SchedulesController {
    private final Logger logger = LoggerFactory.getLogger(SchedulesController.class);

    private final UserService userService;
    private final ScheduleService scheduleService;
    private final SubjectService subjectService;

    public SchedulesController(UserService userService, ScheduleService scheduleService,
            SubjectService subjectService) {
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.subjectService = subjectService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long id,
            Authentication authentication,
            Model model) {

        String userName = authentication.getName();
        List<Schedule> schedules = scheduleService.findByUserName(userName);
        model.addAttribute("schedules", schedules);

        if (id != null) {
            logger.warn(id.toString());

            scheduleService.findById(id)
                    .ifPresent(s -> {
                        model.addAttribute("selectedSchedule", s);
                    });
        }

        return "pages/home";
    }
}
