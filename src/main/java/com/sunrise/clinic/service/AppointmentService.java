package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.AppointmentForm;
import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.entity.AppointmentStatus;
import com.sunrise.clinic.entity.Dentist;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.AppointmentRepository;
import com.sunrise.clinic.repository.DentistRepository;
import com.sunrise.clinic.repository.PatientRepository;
import com.sunrise.clinic.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Business layer for appointments. All appointment business rules of the
 * scenario are enforced here and not in the controllers.
 */
@Service
@Transactional
public class AppointmentService {

    public static final String DOUBLE_BOOKING_MESSAGE =
            "The selected dentist is already booked for this date and time.";

    private static final DateTimeFormatter NUMBER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DentistRepository dentistRepository,
                              TreatmentRepository treatmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDentist(Long dentistId) {
        return appointmentRepository.findByDentistIdOrderByAppointmentDateDescAppointmentTimeDesc(dentistId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findBetweenDates(LocalDate from, LocalDate to) {
        return appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(from, to);
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found for id " + id));
    }

    /** Search used by the "search by appointment number" screen. */
    @Transactional(readOnly = true)
    public Optional<Appointment> findByAppointmentNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            return Optional.empty();
        }
        return appointmentRepository.findByAppointmentNumber(appointmentNumber.trim());
    }

    @Transactional(readOnly = true)
    public long count() {
        return appointmentRepository.count();
    }

    public Appointment create(AppointmentForm form) {
        Appointment appointment = new Appointment();
        applyForm(appointment, form);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setAppointmentNumber(generateAppointmentNumber());
        return appointmentRepository.save(appointment);
    }

    public Appointment update(Long id, AppointmentForm form) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("A cancelled appointment cannot be updated.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("A completed appointment cannot be updated.");
        }
        applyForm(appointment, form);
        if (form.getStatus() != null) {
            appointment.setStatus(form.getStatus());
        }
        return appointmentRepository.save(appointment);
    }

    public Appointment cancel(Long id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("This appointment has already been cancelled.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("A completed appointment cannot be cancelled.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    /** Copies the form values onto the entity after checking every business rule. */
    private void applyForm(Appointment appointment, AppointmentForm form) {
        if (form.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("An appointment cannot be created for a date in the past.");
        }

        Patient patient = patientRepository.findById(form.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for id " + form.getPatientId()));

        Dentist dentist = dentistRepository.findById(form.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found for id " + form.getDentistId()));
        if (!dentist.isActive()) {
            throw new BusinessRuleException("The selected dentist is not currently active.");
        }

        Treatment treatment = treatmentRepository.findById(form.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found for id " + form.getTreatmentId()));
        if (!treatment.isActive()) {
            throw new BusinessRuleException("The selected treatment is not currently active.");
        }

        checkDentistIsFree(appointment.getId(), dentist.getId(), form.getAppointmentDate(), form.getAppointmentTime());

        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(form.getAppointmentDate());
        appointment.setAppointmentTime(form.getAppointmentTime());
    }

    /**
     * A dentist can only have one appointment per date and time. When an existing
     * appointment is being updated it is allowed to keep its own slot.
     */
    private void checkDentistIsFree(Long currentAppointmentId, Long dentistId, LocalDate date, LocalTime time) {
        Optional<Appointment> slotOwner =
                appointmentRepository.findByDentistIdAndAppointmentDateAndAppointmentTime(dentistId, date, time);
        if (slotOwner.isPresent() && !slotOwner.get().getId().equals(currentAppointmentId)) {
            throw new BusinessRuleException(DOUBLE_BOOKING_MESSAGE);
        }
    }

    /** Builds a unique appointment number such as APT-20260415-0731. */
    private String generateAppointmentNumber() {
        String prefix = "APT-" + LocalDate.now().format(NUMBER_DATE_FORMAT) + "-";
        for (int attempt = 0; attempt < 50; attempt++) {
            String candidate = prefix + String.format("%04d", RANDOM.nextInt(10000));
            if (!appointmentRepository.existsByAppointmentNumber(candidate)) {
                return candidate;
            }
        }
        throw new BusinessRuleException("Could not create a unique appointment number. Please try again.");
    }

    @Transactional(readOnly = true)
    public AppointmentForm toForm(Long id) {
        Appointment appointment = findById(id);
        AppointmentForm form = new AppointmentForm();
        form.setId(appointment.getId());
        form.setAppointmentNumber(appointment.getAppointmentNumber());
        form.setPatientId(appointment.getPatient().getId());
        form.setDentistId(appointment.getDentist().getId());
        form.setTreatmentId(appointment.getTreatment().getId());
        form.setAppointmentDate(appointment.getAppointmentDate());
        form.setAppointmentTime(appointment.getAppointmentTime());
        form.setStatus(appointment.getStatus());
        return form;
    }
}
