package com.sunrise.clinic.dto;

import com.sunrise.clinic.entity.Patient;

/** JSON representation of a patient returned by the REST API. */
public record PatientDto(Long patientId, String name, String address, String contactNumber) {

    public static PatientDto from(Patient patient) {
        return new PatientDto(patient.getId(), patient.getName(), patient.getAddress(), patient.getContactNumber());
    }
}
