package com.sunrise.clinic.service;

import com.sunrise.clinic.entity.Appointment;
import com.sunrise.clinic.entity.AppointmentStatus;
import com.sunrise.clinic.entity.Bill;
import com.sunrise.clinic.exception.BusinessRuleException;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.AppointmentRepository;
import com.sunrise.clinic.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Business layer for billing.
 * Total amount = treatment fee + consultation fee.
 */
@Service
@Transactional
public class BillService {

    private static final DateTimeFormatter NUMBER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;

    public BillService(BillRepository billRepository, AppointmentRepository appointmentRepository) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Bill> findAll() {
        return billRepository.findAllByOrderByGeneratedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Bill> findBetweenDates(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(to, LocalTime.MAX);
        return billRepository.findByGeneratedAtBetweenOrderByGeneratedAtDesc(start, end);
    }

    @Transactional(readOnly = true)
    public Bill findById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for id " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Bill> findByAppointmentId(Long appointmentId) {
        return billRepository.findByAppointmentId(appointmentId);
    }

    @Transactional(readOnly = true)
    public long count() {
        return billRepository.count();
    }

    /** Creates the single bill allowed for an appointment. */
    public Bill generateBill(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found for id " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("A bill cannot be generated for a cancelled appointment.");
        }
        if (billRepository.existsByAppointmentId(appointmentId)) {
            throw new BusinessRuleException("A bill has already been generated for this appointment.");
        }

        BigDecimal treatmentFee = appointment.getTreatment().getTreatmentFee();
        BigDecimal consultationFee = appointment.getTreatment().getConsultationFee();

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber());
        bill.setAppointment(appointment);
        bill.setTreatmentFee(treatmentFee);
        bill.setConsultationFee(consultationFee);
        bill.setTotalAmount(treatmentFee.add(consultationFee));
        bill.setGeneratedAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    /** Builds a unique bill number such as BILL-20260415-0128. */
    private String generateBillNumber() {
        String prefix = "BILL-" + LocalDate.now().format(NUMBER_DATE_FORMAT) + "-";
        for (int attempt = 0; attempt < 50; attempt++) {
            String candidate = prefix + String.format("%04d", RANDOM.nextInt(10000));
            if (!billRepository.existsByBillNumber(candidate)) {
                return candidate;
            }
        }
        throw new BusinessRuleException("Could not create a unique bill number. Please try again.");
    }
}
