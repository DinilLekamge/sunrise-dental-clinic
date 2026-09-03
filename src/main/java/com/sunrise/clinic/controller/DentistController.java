package com.sunrise.clinic.controller;

import com.sunrise.clinic.dto.DentistForm;
import com.sunrise.clinic.entity.Dentist;
import com.sunrise.clinic.service.DentistService;
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
@RequestMapping("/dentists")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dentists", dentistService.findAll());
        return "dentists/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("dentistForm", new DentistForm());
        model.addAttribute("editing", false);
        return "dentists/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("dentistForm", dentistService.toForm(id));
        model.addAttribute("editing", true);
        return "dentists/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("dentistForm") DentistForm dentistForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", dentistForm.getId() != null);
            return "dentists/form";
        }
        Dentist saved = dentistService.save(dentistForm);
        redirectAttributes.addFlashAttribute("successMessage",
                "Dentist " + saved.getName() + " was saved successfully.");
        return "redirect:/dentists";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean active = dentistService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "The dentist is now active." : "The dentist is now inactive.");
        return "redirect:/dentists";
    }
}
