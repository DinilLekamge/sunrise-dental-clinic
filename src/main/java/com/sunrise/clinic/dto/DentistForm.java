package com.sunrise.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DentistForm {

    private Long id;

    @NotBlank(message = "Dentist name is required")
    @Size(max = 100, message = "Dentist name cannot be longer than 100 characters")
    private String name;

    @Size(max = 100, message = "Specialization cannot be longer than 100 characters")
    private String specialization;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[0-9][0-9 -]{6,18}$",
            message = "Contact number must be 7 to 19 digits and may start with +")
    private String contactNumber;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
