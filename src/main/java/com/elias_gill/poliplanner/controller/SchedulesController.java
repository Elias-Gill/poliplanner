package com.elias_gill.poliplanner.controller;

import java.util.List;
import java.util.Objects;

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
import com.elias_gill.poliplanner.exception.InvalidScheduleException;
import com.elias_gill.poliplanner.exception.SubjectNotFoundException;
import com.elias_gill.poliplanner.exception.UserNotFoundException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.SubjectService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SchedulesController {
    private final Logger logger = LoggerFactory.getLogger(SchedulesController.class);

    private final ScheduleService scheduleService;
    private final SubjectService subjectService;
    private final CareerService careerService;

    public SchedulesController(ScheduleService scheduleService,
            SubjectService subjectService, CareerService careerService) {
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

        if (schedules.isEmpty()) {
            return "pages/home";
        }

        // Set the latest created schedule as default
        if (id != null) {
            // FUTURE: maybe migrate to a cookie
            for (Schedule s : schedules) {
                if (s.getId() == id) {
                    model.addAttribute("selectedSchedule", s);
                }
            }
        } else {
            model.addAttribute("selectedSchedule", schedules.get(0));
        }

        return "pages/home";
    }

    @GetMapping("/schedules/new")
    public String showScheduleForm(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Subject> subjects = subjectService.findAll();
            List<Career> careers = careerService.findAll();
            // Extraer y ordenar los semestres
            List<Integer> semesters = subjects.stream()
                    .map(Subject::getSemestre)
                    .filter(Objects::nonNull)
                    .map(s -> {
                        try {
                            return Integer.parseInt(s.trim());
                        } catch (NumberFormatException e) {
                            return null; // o podrías loguearlo
                        }
                    })
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            model.addAttribute("subjects", subjects);
            model.addAttribute("careers", careers);
            model.addAttribute("semestres", semesters);

            return "pages/new_schedule";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "And internal error ocurred. Please try again latter.");
            logger.error("Error on show schedule form: ", e);

            return "redirect:/";
        }
    }

    @PostMapping("/schedules/new")
    public String createSchedule(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "subjectIds", required = false) List<Long> subjectIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            scheduleService.create(userName, description, subjectIds);
            redirectAttributes.addFlashAttribute("success", "Horario creado satisfactoriamente");
            return "redirect:/";
        } catch (UserNotFoundException e) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Usuario no existe, sesión terminada");
            return "redirect:/login";
        } catch (InvalidScheduleException | SubjectNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedules/new";
        } catch (InternalError e) {
            redirectAttributes.addFlashAttribute("error", "Error interno, intenta más tarde");
            logger.error("Error interno al crear horario", e);
            return "redirect:/";
        }
    }

    @PostMapping("/schedules/{id}/delete")
    public String deleteSchedule(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            scheduleService.deleteSchedule(id, username);
            redirectAttributes.addFlashAttribute("success", "Schedule deleted successfully");
        } catch (BadArgumentsException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Invalid argument. Schedule with id = " + id + " does not exist.");
        } catch (InternalServerErrorException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Sorry, an internal server error occurred. Please try again later.");
            logger.error("Error deleting schedule: " + e.getMessage(), e);
        }

        return "redirect:/";
    }
}
