package com.elias_gill.poliplanner.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.elias_gill.poliplanner.models.Schedule;
import com.elias_gill.poliplanner.services.ScheduleService;
import com.elias_gill.poliplanner.services.SheetVersionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class DashboardController {
    private final ScheduleService scheduleService;
    private final SheetVersionService versionService;

    private static final String COOKIE_EXCEL = "ultimaVersionExcelVisto";

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long id,
            @CookieValue(value = COOKIE_EXCEL, defaultValue = "default_value") String cookieVersion,
            HttpServletResponse response,
            Authentication authentication,
            Model model) {

        Long ultimaVersionExcelId = versionService.findLatest().getId();

        if (!String.valueOf(ultimaVersionExcelId).equals(cookieVersion)) {
            response.addCookie(new Cookie(COOKIE_EXCEL, ultimaVersionExcelId.toString()));
            model.addAttribute("hasNewExcel",
                    "📢 ¡Nueva versión del Excel disponible!. Actualiza tu horario usando nuestra "
                            + "<a href=\"#bottom\">herramienta de migración</a>.");
        }

        String userName = authentication.getName();
        List<Schedule> schedules = scheduleService.findByUserName(userName);
        model.addAttribute("schedules", schedules);

        if (schedules.isEmpty()) {
            return "pages/dashboard/home";
        }

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
