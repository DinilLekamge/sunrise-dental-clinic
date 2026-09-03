package com.sunrise.clinic.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TreatmentForm {

    private Long id;

    @NotBlank(message = "Treatment name is required")
    @Size(max = 100, message = "Treatment name cannot be longer than 100 characters")
    private String treatmentName;

    @NotNull(message = "Treatment fee is required")
    @PositiveOrZero(message = "Treatment fee cannot be negative")
    @DecimalMax(value = "99999999.99", message = "Treatment fee is too large")
    private BigDecimal treatmentFee;

    @NotNull(message = "Consultation fee is required")
    @PositiveOrZero(message = "Consultation fee cannot be negative")
    @DecimalMax(value = "99999999.99", message = "Consultation fee is too large")
    private BigDecimal consultationFee;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public BigDecimal getTreatmentFee() {
        return treatmentFee;
    }

    public void setTreatmentFee(BigDecimal treatmentFee) {
        this.treatmentFee = treatmentFee;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
