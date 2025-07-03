package com.elias_gill.poliplanner.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.models.User;
import com.elias_gill.poliplanner.services.CareerService;
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
    private final CareerService careerService;

    public SchedulesController(UserService userService, ScheduleService scheduleService,
            SubjectService subjectService, CareerService careerService) {
        this.userService = userService;
        this.careerService = careerService;
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

        // Set the latest selected schedule as default
        // FUTURE: maybe migrate to a cookie
        if (!schedules.isEmpty()) {
            model.addAttribute("selectedSchedule", schedules.get(0));
        }

        if (id != null) {
            scheduleService.findById(id)
                    .ifPresent(s -> {
                        model.addAttribute("selectedSchedule", s);
                    });
        }

        return "pages/home";
    }

    @GetMapping("/schedules/new")
    public String showScheduleForm(Model model) {
        List<Subject> subjects = subjectService.findAll();
        List<Career> careers = careerService.findAll();

        model.addAttribute("subjects", subjects);
        model.addAttribute("careers", careers);

        return "pages/new_schedule";
    }

    @PostMapping("/schedules/new")
    public String createSchedule(
            @RequestParam("description") String description,
            @RequestParam("subjectIds") List<Long> subjectIds,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Invalidate session if user is not found
        Optional<User> user = userService.findByUsername(userName);
        if (user.isEmpty()) {
            session.invalidate();
            model.addAttribute("error", "Usuario no existe, sesión terminada");
            return "redirect:/login";
        }

        if (description.isEmpty()) {
            model.addAttribute("error", "Se debe proporcionar un nombre para el horario");
            return "redirect:/schedules/new";
        }

        if (subjectIds.isEmpty()) {
            model.addAttribute("error", "Se debe proporcionar un nombre para el horario");
            return "redirect:/schedules/new";
        }

        // Persist the new schedule
        try {
            scheduleService.create(user.get(), description, subjectIds);
            model.addAttribute("success", "Horario creado satisfactoriamente");
        } catch (InternalError e) {
            model.addAttribute("error", "Sorry, an internal server error ocurred. Please try again later");
            logger.error("Internal error en creacion de horario: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/schedules/{id}/delete")
    public String deleteSchedule(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
        } catch (InternalServerErrorException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Sorry, an internal server error ocurred. Please try again later");
            logger.error("Error deleting schedule: " + e.getMessage());
        } catch (BadArgumentsException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Arguments invalid. Schedule with id=" + id.toString() + "does not exists");
        }

        redirectAttributes.addFlashAttribute("success", "Schedule deleted succesfully");
        return "redirect:/";
    }
}
