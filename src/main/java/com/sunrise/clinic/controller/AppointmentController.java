package com.sunrise.clinic.controller;

import com.sunrise.clinic.dto.AppointmentForm;
import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.entity.AppointmentStatus;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.service.AppointmentService;
import com.sunrise.clinic.service.BillService;
import com.sunrise.clinic.service.DentistService;
import com.sunrise.clinic.service.PatientService;
import com.sunrise.clinic.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DentistService dentistService;
    private final TreatmentService treatmentService;
    private final BillService billService;

    public AppointmentController(AppointmentService appointmentService,
                                 PatientService patientService,
                                 DentistService dentistService,
                                 TreatmentService treatmentService,
                                 BillService billService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.treatmentService = treatmentService;
        this.billService = billService;
    }

    /** Lists every appointment, with an optional filter by dentist. */
    @GetMapping
    public String list(@RequestParam(name = "dentistId", required = false) Long dentistId, Model model) {
        List<Appointment> appointments = dentistId == null
                ? appointmentService.findAll()
                : appointmentService.findByDentist(dentistId);
        model.addAttribute("appointments", appointments);
        model.addAttribute("dentists", dentistService.findAll());
        model.addAttribute("selectedDentistId", dentistId);
        return "appointments/list";
    }

    /** Search by appointment number, as required by the scenario. */
    @GetMapping("/search")
    public String search(@RequestParam(name = "number", required = false) String number,
                         RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointment = appointmentService.findByAppointmentNumber(number);
        if (appointment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No appointment was found for number " + (number == null ? "" : number.trim()) + ".");
            return "redirect:/appointments";
        }
        return "redirect:/appointments/" + appointment.get().getId();
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("appointmentForm", new AppointmentForm());
        model.addAttribute("editing", false);
        addSelectionLists(model);
        return "appointments/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.findById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("bill", billService.findByAppointmentId(id).orElse(null));
        return "appointments/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointmentForm", appointmentService.toForm(id));
        model.addAttribute("editing", true);
        addSelectionLists(model);
        return "appointments/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("appointmentForm") AppointmentForm appointmentForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return backToForm(appointmentForm, model);
        }
        try {
            Appointment saved = appointmentForm.getId() == null
                    ? appointmentService.create(appointmentForm)
                    : appointmentService.update(appointmentForm.getId(), appointmentForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + saved.getAppointmentNumber() + " was saved successfully.");
            return "redirect:/appointments/" + saved.getId();
        } catch (BusinessRuleException ex) {
            bindingResult.reject("appointment.error", ex.getMessage());
            return backToForm(appointmentForm, model);
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Appointment cancelled = appointmentService.cancel(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + cancelled.getAppointmentNumber() + " was cancelled.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments/" + id;
    }

    private String backToForm(AppointmentForm appointmentForm, Model model) {
        model.addAttribute("editing", appointmentForm.getId() != null);
        addSelectionLists(model);
        return "appointments/form";
    }

    /** Only active dentists and active treatments may be selected. */
    private void addSelectionLists(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("dentists", dentistService.findActive());
        model.addAttribute("treatments", treatmentService.findActive());
        model.addAttribute("statuses", AppointmentStatus.values());
    }
}
