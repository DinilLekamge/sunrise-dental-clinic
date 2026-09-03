package com.sunrise.clinic.controller;

import com.sunrise.clinic.dto.TreatmentForm;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.exception.BusinessRuleException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("treatments", treatmentService.findAll());
        return "treatments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("treatmentForm", new TreatmentForm());
        model.addAttribute("editing", false);
        return "treatments/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("treatmentForm", treatmentService.toForm(id));
        model.addAttribute("editing", true);
        return "treatments/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("treatmentForm") TreatmentForm treatmentForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", treatmentForm.getId() != null);
            return "treatments/form";
        }
        try {
            Treatment saved = treatmentService.save(treatmentForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Treatment " + saved.getTreatmentName() + " was saved successfully.");
            return "redirect:/treatments";
        } catch (BusinessRuleException ex) {
            bindingResult.reject("treatment.error", ex.getMessage());
            model.addAttribute("editing", treatmentForm.getId() != null);
            return "treatments/form";
        }
    }

    @PostMapping("/{id}/toggle")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean active = treatmentService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "The treatment is now active." : "The treatment is now inactive.");
        return "redirect:/treatments";
    }
}
