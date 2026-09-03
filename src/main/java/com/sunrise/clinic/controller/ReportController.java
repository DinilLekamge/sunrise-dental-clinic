package com.sunrise.clinic.controller;

import com.sunrise.clinic.service.AppointmentService;
import com.sunrise.clinic.service.BillService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Simple management reports. Both reports can be filtered by a date range.
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final AppointmentService appointmentService;
    private final BillService billService;

    public ReportController(AppointmentService appointmentService, BillService billService) {
        this.appointmentService = appointmentService;
        this.billService = billService;
    }

    @GetMapping
    public String index() {
        return "redirect:/reports/appointments";
    }

    @GetMapping("/appointments")
    public String appointmentReport(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("appointments", from != null && to != null
                ? appointmentService.findBetweenDates(from, to)
                : appointmentService.findAll());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "reports/appointments";
    }

    @GetMapping("/bills")
    public String billingReport(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("bills", from != null && to != null
                ? billService.findBetweenDates(from, to)
                : billService.findAll());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "reports/bills";
    }
}
