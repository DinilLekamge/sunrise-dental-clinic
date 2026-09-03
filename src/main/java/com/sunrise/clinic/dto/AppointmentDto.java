package com.sunrise.clinic.dto;

import com.sunrise.clinic.entity.Appointment;

import java.math.BigDecimal;

/** JSON representation of an appointment returned by the REST API. */
public record AppointmentDto(String appointmentNumber,
                             String patientName,
                             String patientContactNumber,
                             String dentistName,
                             String treatmentName,
                             BigDecimal treatmentFee,
                             BigDecimal consultationFee,
                             String appointmentDate,
                             String appointmentTime,
                             String status) {

    public static AppointmentDto from(Appointment appointment) {
        return new AppointmentDto(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getName(),
                appointment.getPatient().getContactNumber(),
                appointment.getDentist().getName(),
                appointment.getTreatment().getTreatmentName(),
                appointment.getTreatment().getTreatmentFee(),
                appointment.getTreatment().getConsultationFee(),
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString(),
                appointment.getStatus().name());
    }
}
