package com.sunrise.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientForm {

    private Long id;

    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name cannot be longer than 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot be longer than 255 characters")
    private String address;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[0-9][0-9 -]{6,18}$",
            message = "Contact number must be 7 to 19 digits and may start with +")
    private String contactNumber;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
