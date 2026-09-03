package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.DentistForm;
import com.sunrise.clinic.entity.Dentist;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.DentistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DentistService {

    private final DentistRepository dentistRepository;

    public DentistService(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @Transactional(readOnly = true)
    public List<Dentist> findAll() {
        return dentistRepository.findAllByOrderByNameAsc();
    }

    /** Only active dentists may be selected when booking an appointment. */
    @Transactional(readOnly = true)
    public List<Dentist> findActive() {
        return dentistRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Dentist findById(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found for id " + id));
    }

    @Transactional(readOnly = true)
    public long count() {
        return dentistRepository.count();
    }

    public Dentist save(DentistForm form) {
        Dentist dentist = form.getId() == null ? new Dentist() : findById(form.getId());
        dentist.setName(form.getName().trim());
        dentist.setSpecialization(form.getSpecialization() == null ? null : form.getSpecialization().trim());
        dentist.setContactNumber(form.getContactNumber().trim());
        dentist.setActive(form.isActive());
        return dentistRepository.save(dentist);
    }

    public boolean toggleActive(Long id) {
        Dentist dentist = findById(id);
        dentist.setActive(!dentist.isActive());
        dentistRepository.save(dentist);
        return dentist.isActive();
    }

    @Transactional(readOnly = true)
    public DentistForm toForm(Long id) {
        Dentist dentist = findById(id);
        DentistForm form = new DentistForm();
        form.setId(dentist.getId());
        form.setName(dentist.getName());
        form.setSpecialization(dentist.getSpecialization());
        form.setContactNumber(dentist.getContactNumber());
        form.setActive(dentist.isActive());
        return form;
    }
}
