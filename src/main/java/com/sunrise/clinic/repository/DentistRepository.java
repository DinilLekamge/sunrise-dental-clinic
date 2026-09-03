package com.sunrise.clinic.repository;

import com.sunrise.clinic.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentistRepository extends JpaRepository<Dentist, Long> {

    List<Dentist> findAllByOrderByNameAsc();

    List<Dentist> findByActiveTrueOrderByNameAsc();
}
