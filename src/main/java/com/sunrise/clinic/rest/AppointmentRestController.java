package com.sunrise.clinic.rest;

import com.sunrise.clinic.dto.AppointmentDto;
import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.service.AppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web service endpoints for appointments.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    public AppointmentRestController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentDto> findAll() {
        return appointmentService.findAll().stream().map(AppointmentDto::from).toList();
    }

    @GetMapping("/{appointmentNumber}")
    public AppointmentDto findByNumber(@PathVariable String appointmentNumber) {
        Appointment appointment = appointmentService.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found for number " + appointmentNumber));
        return AppointmentDto.from(appointment);
    }
}
