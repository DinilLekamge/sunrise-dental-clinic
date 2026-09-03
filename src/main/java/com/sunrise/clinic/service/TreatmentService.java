package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.TreatmentForm;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Treatment> findAll() {
        return treatmentRepository.findAllByOrderByTreatmentNameAsc();
    }

    /** Only active treatments may be selected when booking an appointment. */
    @Transactional(readOnly = true)
    public List<Treatment> findActive() {
        return treatmentRepository.findByActiveTrueOrderByTreatmentNameAsc();
    }

    @Transactional(readOnly = true)
    public Treatment findById(Long id) {
        return treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found for id " + id));
    }

    @Transactional(readOnly = true)
    public long count() {
        return treatmentRepository.count();
    }

    public Treatment save(TreatmentForm form) {
        String name = form.getTreatmentName().trim();
        Optional<Treatment> existingWithSameName = treatmentRepository.findByTreatmentNameIgnoreCase(name);
        if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(form.getId())) {
            throw new BusinessRuleException("A treatment with this name already exists.");
        }

        Treatment treatment = form.getId() == null ? new Treatment() : findById(form.getId());
        treatment.setTreatmentName(name);
        treatment.setTreatmentFee(form.getTreatmentFee());
        treatment.setConsultationFee(form.getConsultationFee());
        treatment.setActive(form.isActive());
        return treatmentRepository.save(treatment);
    }

    public boolean toggleActive(Long id) {
        Treatment treatment = findById(id);
        treatment.setActive(!treatment.isActive());
        treatmentRepository.save(treatment);
        return treatment.isActive();
    }

    @Transactional(readOnly = true)
    public TreatmentForm toForm(Long id) {
        Treatment treatment = findById(id);
        TreatmentForm form = new TreatmentForm();
        form.setId(treatment.getId());
        form.setTreatmentName(treatment.getTreatmentName());
        form.setTreatmentFee(treatment.getTreatmentFee());
        form.setConsultationFee(treatment.getConsultationFee());
        form.setActive(treatment.isActive());
        return form;
    }
}
