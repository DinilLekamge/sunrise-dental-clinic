package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.PatientForm;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business layer for patient registration and patient searching.
 */
@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for id " + id));
    }

    @Transactional(readOnly = true)
    public List<Patient> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String term = query.trim();
        return patientRepository.findByNameContainingIgnoreCaseOrContactNumberContaining(term, term);
    }

    @Transactional(readOnly = true)
    public long count() {
        return patientRepository.count();
    }

    /** Creates a new patient, or updates the existing one when the form carries an id. */
    public Patient save(PatientForm form) {
        Patient patient = form.getId() == null ? new Patient() : findById(form.getId());
        patient.setName(form.getName().trim());
        patient.setAddress(form.getAddress().trim());
        patient.setContactNumber(form.getContactNumber().trim());
        return patientRepository.save(patient);
    }

    /** Fills a form with the values of an existing patient so it can be edited. */
    @Transactional(readOnly = true)
    public PatientForm toForm(Long id) {
        Patient patient = findById(id);
        PatientForm form = new PatientForm();
        form.setId(patient.getId());
        form.setName(patient.getName());
        form.setAddress(patient.getAddress());
        form.setContactNumber(patient.getContactNumber());
        return form;
    }
}
