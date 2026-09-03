package com.sunrise.clinic.controller;

import com.sunrise.clinic.entity.Bill;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.service.BillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillService billService;

    public BillingController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bills", billService.findAll());
        return "billing/list";
    }

    @PostMapping("/generate/{appointmentId}")
    public String generate(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            Bill bill = billService.generateBill(appointmentId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bill " + bill.getBillNumber() + " was generated.");
            return "redirect:/billing/" + bill.getId();
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/appointments/" + appointmentId;
        }
    }

    @GetMapping("/{id}")
    public String receipt(@PathVariable Long id, Model model) {
        model.addAttribute("bill", billService.findById(id));
        return "billing/receipt";
    }
}
