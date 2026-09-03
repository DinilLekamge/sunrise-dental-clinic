package com.sunrise.clinic.controller;

import com.sunrise.clinic.dto.UserForm;
import com.sunrise.clinic.entity.Role;
import com.sunrise.clinic.entity.User;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("roles", Role.values());
        model.addAttribute("editing", false);
        return "users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("userForm", userService.toForm(id));
        model.addAttribute("roles", Role.values());
        model.addAttribute("editing", true);
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("userForm") UserForm userForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", userForm.getId() != null);
            return "users/form";
        }
        try {
            User saved = userService.save(userForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + saved.getUsername() + " was saved successfully.");
            return "redirect:/users";
        } catch (BusinessRuleException ex) {
            bindingResult.reject("user.error", ex.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", userForm.getId() != null);
            return "users/form";
        }
    }

    @PostMapping("/{id}/toggle")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean active = userService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "The user account is now active." : "The user account is now inactive.");
        return "redirect:/users";
    }
}
