package com.sunrise.clinic.repository;

import com.sunrise.clinic.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByBillNumber(String billNumber);

    List<Bill> findAllByOrderByGeneratedAtDesc();

    List<Bill> findByGeneratedAtBetweenOrderByGeneratedAtDesc(LocalDateTime from, LocalDateTime to);
}
