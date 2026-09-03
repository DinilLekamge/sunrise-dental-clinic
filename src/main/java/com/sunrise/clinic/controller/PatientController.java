package com.sunrise.clinic.controller;

import com.sunrise.clinic.dto.PatientForm;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.service.PatientService;
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

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public String list(@RequestParam(name = "query", required = false) String query, Model model) {
        model.addAttribute("patients", patientService.search(query));
        model.addAttribute("query", query);
        return "patients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        model.addAttribute("editing", false);
        return "patients/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id);
        model.addAttribute("patient", patient);
        return "patients/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patientForm", patientService.toForm(id));
        model.addAttribute("editing", true);
        return "patients/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("patientForm") PatientForm patientForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", patientForm.getId() != null);
            return "patients/form";
        }
        Patient saved = patientService.save(patientForm);
        redirectAttributes.addFlashAttribute("successMessage",
                "Patient " + saved.getName() + " was saved successfully.");
        return "redirect:/patients/" + saved.getId();
    }
}
