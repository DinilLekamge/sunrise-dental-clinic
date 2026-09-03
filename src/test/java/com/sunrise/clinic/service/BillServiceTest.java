package com.sunrise.clinic.service;

import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.entity.AppointmentStatus;
import com.sunrise.clinic.entity.Bill;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.repository.AppointmentRepository;
import com.sunrise.clinic.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests for the bill total and for the one bill per appointment rule. */
@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private BillService billService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Treatment treatment = new Treatment();
        treatment.setId(3L);
        treatment.setTreatmentName("Tooth Filling");
        treatment.setTreatmentFee(new BigDecimal("8000.00"));
        treatment.setConsultationFee(new BigDecimal("2000.00"));

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Amara Jayasuriya");

        appointment = new Appointment();
        appointment.setId(10L);
        appointment.setAppointmentNumber("APT-20260101-0010");
        appointment.setPatient(patient);
        appointment.setTreatment(treatment);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    void totalAmountIsTheTreatmentFeePlusTheConsultationFee() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(billRepository.existsByAppointmentId(10L)).thenReturn(false);
        when(billRepository.existsByBillNumber(anyString())).thenReturn(false);
        when(billRepository.save(any(Bill.class))).thenAnswer(call -> call.getArgument(0));

        Bill bill = billService.generateBill(10L);

        assertThat(bill.getTreatmentFee()).isEqualByComparingTo("8000.00");
        assertThat(bill.getConsultationFee()).isEqualByComparingTo("2000.00");
        assertThat(bill.getTotalAmount()).isEqualByComparingTo("10000.00");
        assertThat(bill.getBillNumber()).startsWith("BILL-");
        assertThat(bill.getGeneratedAt()).isNotNull();
    }

    @Test
    void refusesASecondBillForTheSameAppointment() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(billRepository.existsByAppointmentId(10L)).thenReturn(true);

        assertThatThrownBy(() -> billService.generateBill(10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already been generated");

        verify(billRepository, never()).save(any(Bill.class));
    }

    @Test
    void refusesABillForACancelledAppointment() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> billService.generateBill(10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cancelled");

        verify(billRepository, never()).save(any(Bill.class));
    }
}
