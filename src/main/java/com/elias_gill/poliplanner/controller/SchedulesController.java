package com.elias_gill.poliplanner.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.exception.InvalidScheduleException;
import com.elias_gill.poliplanner.exception.SubjectNotFoundException;
import com.elias_gill.poliplanner.exception.UserNotFoundException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.Subject;
import com.elias_gill.poliplanner.services.CareerService;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.SheetVersionService;
import com.elias_gill.poliplanner.services.SubjectService;

import jakarta.servlet.http.HttpSession;

/**
 * FIX: comentario choto de IA
 * Controlador responsable del flujo de creación de un nuevo horario académico.
 * <p>
 *
 * El proceso se divide en dos pasos principales:
 *
 * 1. Selección de carrera y descripción del horario:
 * - GET /schedule/new: muestra un formulario con la lista de carreras
 * disponibles, muestra la informacion de la ultima plantilla excel parseada y
 * pide el ingreso de un nuevo nombre para el horario.
 * - POST /schedule/new: recibe la carrera seleccionada y la descripción,
 * luego redirige al siguiente paso pasando los datos como parámetros.
 * <p>
 *
 * 2. Selección de materias:
 * - GET /schedule/new/details: muestra las materias correspondientes a la
 * carrera elegida y permite al usuario armar su horario seleccionando
 * asignaturas.
 * - POST /schedule/new/details: recibe la lista de materias seleccionadas y
 * crea el horario en la base de datos.
 * <p>
 *
 * Notas:
 * - Se usa el nombre de usuario autenticado para asociar el horario al usuario.
 * - Se utiliza PRG (Post/Redirect/Get) para evitar reenvíos accidentales al
 * recargar.
 * - En caso de errores de validación o sistema, se redirige con mensajes
 * adecuados.
 * <p>
 *
 * Esto permite una experiencia de usuario sencilla y facilita la
 * validacion progresiva de los datos ingresados.
 */
@Controller
@RequestMapping("/schedule")
public class SchedulesController {
    private final static Logger logger = LoggerFactory.getLogger(SchedulesController.class);

    private final ScheduleService scheduleService;
    private final SubjectService subjectService;
    private final CareerService careerService;

    public SchedulesController(ScheduleService scheduleService,
            SubjectService subjectService, CareerService careerService, SheetVersionService sheetVersionService) {
        this.careerService = careerService;
        this.scheduleService = scheduleService;
        this.subjectService = subjectService;
    }

    // Mostrar formulario para seleccionar carrera y descripción
    @GetMapping("/new")
    public String showScheduleForm(Model model) {
        List<Career> careers = careerService.findCareers();

        if (careers.isEmpty()) {
            model.addAttribute("error", "No hay carreras disponibles actualmente.");
            return "pages/schedules/creation_form";
        }

        model.addAttribute("careers", careers);
        model.addAttribute("sheetVersion", careers.get(0).getVersion()); // Por defecto
        return "pages/schedules/creation_form";
    }

    // Recibir descripción y carrera, redirigir al formulario de detalle
    @PostMapping("/new")
    public String createSchedule(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "careerId", required = false) Long careerId,
            RedirectAttributes redirectAttributes) {

        if (careerId == null || description == null || description.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar una descripción y seleccionar una carrera.");
            return "redirect:/schedule/new";
        }

        // Pasar como query params
        return "redirect:/schedule/new/details?careerId=" + careerId + "&description="
                + UriUtils.encode(description, StandardCharsets.UTF_8);
    }

    // Mostrar formulario de detalle con materias
    @GetMapping("/new/details")
    public String showScheduleDetailForm(Model model,
            @RequestParam(name = "careerId", required = false) Long careerId,
            @RequestParam(name = "description", required = false) String description,
            RedirectAttributes redirectAttributes) {

        if (careerId == null || description == null || description.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Información incompleta. Por favor, vuelva a empezar.");
            return "redirect:/schedule/new";
        }

        try {
            Map<Integer, Map<String, List<Subject>>> subjects = subjectService.findByCareer(careerId);

            model.addAttribute("subjects", subjects);
            model.addAttribute("description", description);
            model.addAttribute("careerId", careerId);
            return "pages/schedules/details_form";
        } catch (Exception e) {
            logger.error("Error al mostrar materias para la carrera con ID: " + careerId, e);
            redirectAttributes.addFlashAttribute("error", "Error interno. Intenta más tarde.");
            return "redirect:/";
        }
    }

    // Crear el horario con materias seleccionadas
    @PostMapping("/new/details")
    public String createScheduleDetails(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "subjectIds", required = false) List<Long> subjectIds,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (subjectIds == null || subjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar al menos una materia.");
            return "redirect:/schedule/new/details?description=" + UriUtils.encode(description, StandardCharsets.UTF_8);
        }

        try {
            scheduleService.create(userName, description, subjectIds);
            redirectAttributes.addFlashAttribute("success", "Horario creado exitosamente.");
            return "redirect:/";
        } catch (UserNotFoundException e) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "El usuario no existe. Inicia sesión nuevamente.");
            return "redirect:/login";
        } catch (InvalidScheduleException | SubjectNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedule/new/details?description=" + UriUtils.encode(description, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error al crear el horario", e);
            redirectAttributes.addFlashAttribute("error", "Error interno. Intenta más tarde.");
            return "redirect:/";
        }
    }

    @PostMapping("/{id}/delete")
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
