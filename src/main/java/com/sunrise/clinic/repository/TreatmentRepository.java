package com.sunrise.clinic.repository;

import com.sunrise.clinic.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findAllByOrderByTreatmentNameAsc();

    List<Treatment> findByActiveTrueOrderByTreatmentNameAsc();

    Optional<Treatment> findByTreatmentNameIgnoreCase(String treatmentName);
}
