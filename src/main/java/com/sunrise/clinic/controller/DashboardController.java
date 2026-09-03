package com.sunrise.clinic.controller;

import com.sunrise.clinic.service.AppointmentService;
import com.sunrise.clinic.service.BillService;
import com.sunrise.clinic.service.DentistService;
import com.sunrise.clinic.service.PatientService;
import com.sunrise.clinic.service.TreatmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final DentistService dentistService;
    private final TreatmentService treatmentService;
    private final BillService billService;

    public DashboardController(PatientService patientService,
                               AppointmentService appointmentService,
                               DentistService dentistService,
                               TreatmentService treatmentService,
                               BillService billService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.dentistService = dentistService;
        this.treatmentService = treatmentService;
        this.billService = billService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("patientCount", patientService.count());
        model.addAttribute("appointmentCount", appointmentService.count());
        model.addAttribute("dentistCount", dentistService.count());
        model.addAttribute("treatmentCount", treatmentService.count());
        model.addAttribute("billCount", billService.count());
        return "dashboard";
    }

    @GetMapping("/help")
    public String help() {
        return "help";
    }
}
