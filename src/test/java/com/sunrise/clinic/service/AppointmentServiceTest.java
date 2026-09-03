package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.AppointmentForm;
import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.entity.AppointmentStatus;
import com.sunrise.clinic.entity.Dentist;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.repository.AppointmentRepository;
import com.sunrise.clinic.repository.DentistRepository;
import com.sunrise.clinic.repository.PatientRepository;
import com.sunrise.clinic.repository.TreatmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the appointment business rules: double booking, past dates,
 * inactive dentists and searching by appointment number.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DentistRepository dentistRepository;
    @Mock
    private TreatmentRepository treatmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Amara Jayasuriya");

        dentist = new Dentist();
        dentist.setId(2L);
        dentist.setName("Dr. Nimal Perera");
        dentist.setActive(true);

        treatment = new Treatment();
        treatment.setId(3L);
        treatment.setTreatmentName("Tooth Filling");
        treatment.setTreatmentFee(new BigDecimal("8000.00"));
        treatment.setConsultationFee(new BigDecimal("2000.00"));
        treatment.setActive(true);

        futureDate = LocalDate.now().plusDays(3);
    }

    private AppointmentForm buildForm() {
        AppointmentForm form = new AppointmentForm();
        form.setPatientId(1L);
        form.setDentistId(2L);
        form.setTreatmentId(3L);
        form.setAppointmentDate(futureDate);
        form.setAppointmentTime(LocalTime.of(10, 30));
        return form;
    }

    private void stubReferenceData() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(2L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(3L)).thenReturn(Optional.of(treatment));
    }

    @Test
    void createsAnAppointmentWithAUniqueNumberAndScheduledStatus() {
        stubReferenceData();
        when(appointmentRepository.findByDentistIdAndAppointmentDateAndAppointmentTime(
                2L, futureDate, LocalTime.of(10, 30))).thenReturn(Optional.empty());
        when(appointmentRepository.existsByAppointmentNumber(anyString())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(call -> call.getArgument(0));

        Appointment created = appointmentService.create(buildForm());

        assertThat(created.getAppointmentNumber()).startsWith("APT-");
        assertThat(created.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(created.getPatient()).isSameAs(patient);
        assertThat(created.getDentist()).isSameAs(dentist);
        assertThat(created.getTreatment()).isSameAs(treatment);
        assertThat(created.getAppointmentTime()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    void rejectsASecondAppointmentForTheSameDentistDateAndTime() {
        stubReferenceData();
        Appointment alreadyBooked = new Appointment();
        alreadyBooked.setId(55L);
        when(appointmentRepository.findByDentistIdAndAppointmentDateAndAppointmentTime(
                2L, futureDate, LocalTime.of(10, 30))).thenReturn(Optional.of(alreadyBooked));

        assertThatThrownBy(() -> appointmentService.create(buildForm()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("The selected dentist is already booked for this date and time.");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsAnAppointmentInThePast() {
        AppointmentForm form = buildForm();
        form.setAppointmentDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> appointmentService.create(form))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("past");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsAnAppointmentForAnInactiveDentist() {
        dentist.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(2L)).thenReturn(Optional.of(dentist));

        assertThatThrownBy(() -> appointmentService.create(buildForm()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not currently active");
    }

    @Test
    void findsAnAppointmentByItsNumber() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("APT-20260101-0001");
        when(appointmentRepository.findByAppointmentNumber("APT-20260101-0001"))
                .thenReturn(Optional.of(appointment));

        Optional<Appointment> found = appointmentService.findByAppointmentNumber("  APT-20260101-0001 ");

        assertThat(found).containsSame(appointment);
    }

    @Test
    void anEmptySearchTermFindsNothing() {
        assertThat(appointmentService.findByAppointmentNumber("  ")).isEmpty();
        verify(appointmentRepository, never()).findByAppointmentNumber(anyString());
    }

    @Test
    void cancellingAnAppointmentSetsTheCancelledStatus() {
        Appointment appointment = new Appointment();
        appointment.setId(9L);
        appointment.setAppointmentNumber("APT-20260101-0009");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findById(9L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(call -> call.getArgument(0));

        Appointment cancelled = appointmentService.cancel(9L);

        assertThat(cancelled.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }
    @Test
    void rejectsAnAppointmentForAnInactiveTreatment() {
        stubReferenceData();
        treatment.setActive(false);

        assertThatThrownBy(() -> appointmentService.create(buildForm()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not currently active");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsUpdatingACancelledAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(20L);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(20L))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.update(20L, buildForm()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cancelled");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsCancellingAnAlreadyCancelledAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(21L);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(21L))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancel(21L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already been cancelled");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void rejectsCancellingACompletedAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(22L);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(22L))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancel(22L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("completed");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
    @Test
    void rejectsUpdatingACompletedAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(23L);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(23L))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.update(23L, buildForm()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("completed");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
