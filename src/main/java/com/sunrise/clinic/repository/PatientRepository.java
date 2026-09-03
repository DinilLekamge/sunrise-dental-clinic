package com.sunrise.clinic.repository;

import com.sunrise.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAllByOrderByNameAsc();

    List<Patient> findByNameContainingIgnoreCaseOrContactNumberContaining(String name, String contactNumber);
}
