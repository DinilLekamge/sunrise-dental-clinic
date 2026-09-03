package com.sunrise.clinic.repository;

import com.sunrise.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByAppointmentNumber(String appointmentNumber);

    /**
     * Used to enforce the business rule that a dentist cannot be booked twice
     * for the same date and time.
     */
    Optional<Appointment> findByDentistIdAndAppointmentDateAndAppointmentTime(Long dentistId,
                                                                             LocalDate appointmentDate,
                                                                             LocalTime appointmentTime);

    List<Appointment> findAllByOrderByAppointmentDateDescAppointmentTimeDesc();

    List<Appointment> findByDentistIdOrderByAppointmentDateDescAppointmentTimeDesc(Long dentistId);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(LocalDate from,
                                                                                             LocalDate to);
}
