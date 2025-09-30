package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.service.StatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class StatsController {
    StatsService statsService;

    @GetMapping("/stats")
    public String stats(Model model, @RequestParam Map<String, String> params) {
        Integer year = params.get("year") != null && !params.get("year").isEmpty() ? Integer.valueOf(params.get("year")) : null;
        Integer quarter = params.get("quarter") != null && !params.get("quarter").isEmpty() ? Integer.valueOf(params.get("quarter")) : null;
        Integer month = params.get("month") != null && !params.get("month").isEmpty() ? Integer.valueOf(params.get("month")) : null;
        List<Object[]> statsAdmin = statsService.statsCountExaminedTotalAmount(year, quarter, month);
        List<Object[]> statsDoctor = statsService.statsDiagnosedCountExamined(year, quarter, month);
        model.addAttribute("stats_admin", statsAdmin);
        model.addAttribute("stats_doctor", statsDoctor);
        model.addAttribute("params", params);
        return "stats/stats";
    }
}
