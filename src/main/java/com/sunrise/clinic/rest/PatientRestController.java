package com.sunrise.clinic.rest;

import com.sunrise.clinic.dto.PatientDto;
import com.sunrise.clinic.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web service endpoints for patients. Only safe fields are returned as JSON.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientRestController {

    private final PatientService patientService;

    public PatientRestController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll().stream().map(PatientDto::from).toList();
    }

    @GetMapping("/{id}")
    public PatientDto findById(@PathVariable Long id) {
        return PatientDto.from(patientService.findById(id));
    }
}
