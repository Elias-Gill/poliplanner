package com.elias_gill.poliplanner.controller;

import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.UserService;

import org.springframework.stereotype.Controller;

@Controller
public class ScheduleController {

    private final UserService userService;
    private final ScheduleService scheduleService;

    public ScheduleController(UserService userService, ScheduleService scheduleService) {
        this.userService = userService;
        this.scheduleService = scheduleService;
    }
}
