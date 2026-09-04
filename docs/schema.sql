-- Reference copy of the database used by the application.
-- The database and tables already exist in the developer environment; this file
-- is kept so the schema can be recreated on another machine if needed.

CREATE DATABASE IF NOT EXISTS sunrise_dental_clinic
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_clinic;

CREATE TABLE IF NOT EXISTS users (
    user_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','RECEPTIONIST','DENTIST') NOT NULL,
    active        BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS patients (
    patient_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS dentists (
    dentist_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    contact_number VARCHAR(20) NOT NULL,
    active         BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS treatments (
    treatment_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    treatment_name   VARCHAR(100) UNIQUE NOT NULL,
    treatment_fee    DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    active           BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS appointments (
                                            appointment_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            appointment_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id         BIGINT NOT NULL,
    dentist_id         BIGINT NOT NULL,
    treatment_id       BIGINT NOT NULL,
    appointment_date   DATE NOT NULL,
    appointment_time   TIME NOT NULL,
    status             ENUM('SCHEDULED','COMPLETED','CANCELLED')
    DEFAULT 'SCHEDULED',
    CONSTRAINT fk_appointment_patient
    FOREIGN KEY (patient_id)
    REFERENCES patients(patient_id),
    CONSTRAINT fk_appointment_dentist
    FOREIGN KEY (dentist_id)
    REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appointment_treatment
    FOREIGN KEY (treatment_id)
    REFERENCES treatments(treatment_id),
    CONSTRAINT uq_dentist_timeslot
    UNIQUE (dentist_id, appointment_date, appointment_time)
    );

CREATE TABLE IF NOT EXISTS bills (
                                     bill_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     bill_number      VARCHAR(50) UNIQUE NOT NULL,
    appointment_id   BIGINT UNIQUE NOT NULL,
    treatment_fee    DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    total_amount     DECIMAL(10,2) NOT NULL,
    generated_at     DATETIME,
    CONSTRAINT fk_bill_appointment
    FOREIGN KEY (appointment_id)
    REFERENCES appointments(appointment_id),
    CHECK (treatment_fee >= 0),
    CHECK (consultation_fee >= 0),
    CHECK (total_amount >= 0)
    );
-- ============================================================
-- Stored Procedure: Get Appointment Details
-- Returns complete appointment information for a supplied
-- appointment number.
-- ============================================================

DELIMITER //

CREATE PROCEDURE GetAppointmentDetails(IN p_appointment_number VARCHAR(50))
BEGIN
SELECT
    a.appointment_number,
    p.name AS patient_name,
    p.address,
    p.contact_number,
    d.name AS dentist_name,
    d.specialization,
    t.treatment_name,
    t.treatment_fee,
    t.consultation_fee,
    a.appointment_date,
    a.appointment_time,
    a.status
FROM appointments a
         INNER JOIN patients p
                    ON a.patient_id = p.patient_id
         INNER JOIN dentists d
                    ON a.dentist_id = d.dentist_id
         INNER JOIN treatments t
                    ON a.treatment_id = t.treatment_id
WHERE a.appointment_number = p_appointment_number;
END //

DELIMITER ;

