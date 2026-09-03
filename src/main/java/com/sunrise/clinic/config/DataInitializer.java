package com.sunrise.clinic.config;

import com.sunrise.clinic.entity.Dentist;
import com.sunrise.clinic.entity.Role;
import com.sunrise.clinic.entity.Treatment;
import com.sunrise.clinic.entity.User;
import com.sunrise.clinic.repository.DentistRepository;
import com.sunrise.clinic.repository.TreatmentRepository;
import com.sunrise.clinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Creates the demonstration accounts and some sample data the first time the
 * application starts. Passwords are read from application.properties and are
 * hashed with BCrypt before they are stored, so no plain password ever reaches
 * the database.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean demoDataEnabled;
    private final String adminPassword;
    private final String receptionistPassword;
    private final String dentistPassword;

    public DataInitializer(UserRepository userRepository,
                           DentistRepository dentistRepository,
                           TreatmentRepository treatmentRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.demo.data-enabled:true}") boolean demoDataEnabled,
                           @Value("${app.demo.admin-password}") String adminPassword,
                           @Value("${app.demo.receptionist-password}") String receptionistPassword,
                           @Value("${app.demo.dentist-password}") String dentistPassword) {
        this.userRepository = userRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoDataEnabled = demoDataEnabled;
        this.adminPassword = adminPassword;
        this.receptionistPassword = receptionistPassword;
        this.dentistPassword = dentistPassword;
    }

    @Override
    public void run(String... args) {
        if (!demoDataEnabled) {
            return;
        }
        createDemoUsers();
        createSampleDentists();
        createSampleTreatments();
    }

    private void createDemoUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(buildUser("admin", adminPassword, Role.ADMIN));
        userRepository.save(buildUser("reception", receptionistPassword, Role.RECEPTIONIST));
        userRepository.save(buildUser("dentist", dentistPassword, Role.DENTIST));
    }

    private User buildUser(String username, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private void createSampleDentists() {
        if (dentistRepository.count() > 0) {
            return;
        }
        dentistRepository.save(buildDentist("Dr. Nimal Perera", "General Dentistry", "0112345671"));
        dentistRepository.save(buildDentist("Dr. Anusha Silva", "Orthodontics", "0112345672"));
        dentistRepository.save(buildDentist("Dr. Kasun Fernando", "Oral Surgery", "0112345673"));
    }

    private Dentist buildDentist(String name, String specialization, String contactNumber) {
        Dentist dentist = new Dentist();
        dentist.setName(name);
        dentist.setSpecialization(specialization);
        dentist.setContactNumber(contactNumber);
        dentist.setActive(true);
        return dentist;
    }

    private void createSampleTreatments() {
        if (treatmentRepository.count() > 0) {
            return;
        }
        treatmentRepository.save(buildTreatment("Scaling and Polishing", "5000.00", "2000.00"));
        treatmentRepository.save(buildTreatment("Tooth Filling", "8000.00", "2000.00"));
        treatmentRepository.save(buildTreatment("Root Canal Treatment", "25000.00", "2500.00"));
        treatmentRepository.save(buildTreatment("Tooth Extraction", "6500.00", "2000.00"));
    }

    private Treatment buildTreatment(String name, String treatmentFee, String consultationFee) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentName(name);
        treatment.setTreatmentFee(new BigDecimal(treatmentFee));
        treatment.setConsultationFee(new BigDecimal(consultationFee));
        treatment.setActive(true);
        return treatment;
    }
}
