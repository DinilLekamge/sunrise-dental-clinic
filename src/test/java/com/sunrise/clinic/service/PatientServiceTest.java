package com.sunrise.clinic.service;

import com.sunrise.clinic.dto.PatientForm;
import com.sunrise.clinic.entity.Patient;
import com.sunrise.clinic.exception.ResourceNotFoundException;
import com.sunrise.clinic.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Tests for patient registration and patient searching. */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void savesANewPatientWithTheValuesFromTheForm() {
        PatientForm form = new PatientForm();
        form.setName("  Amara Jayasuriya ");
        form.setAddress(" 12 Galle Road, Colombo ");
        form.setContactNumber(" 0771234567 ");

        when(patientRepository.save(any(Patient.class))).thenAnswer(call -> call.getArgument(0));

        patientService.save(form);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        Patient saved = captor.getValue();

        assertThat(saved.getId()).isNull();
        assertThat(saved.getName()).isEqualTo("Amara Jayasuriya");
        assertThat(saved.getAddress()).isEqualTo("12 Galle Road, Colombo");
        assertThat(saved.getContactNumber()).isEqualTo("0771234567");
    }

    @Test
    void updatesTheExistingPatientWhenTheFormCarriesAnId() {
        Patient existing = new Patient();
        existing.setId(7L);
        existing.setName("Old Name");
        existing.setAddress("Old Address");
        existing.setContactNumber("0111111111");

        when(patientRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(call -> call.getArgument(0));

        PatientForm form = new PatientForm();
        form.setId(7L);
        form.setName("New Name");
        form.setAddress("New Address");
        form.setContactNumber("0772222222");

        Patient saved = patientService.save(form);

        assertThat(saved.getId()).isEqualTo(7L);
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getContactNumber()).isEqualTo("0772222222");
    }

    @Test
    void searchUsesTheSameTermForNameAndContactNumber() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Nadeesha");
        when(patientRepository.findByNameContainingIgnoreCaseOrContactNumberContaining("Nadee", "Nadee"))
                .thenReturn(List.of(patient));

        List<Patient> results = patientService.search("  Nadee ");

        assertThat(results).containsExactly(patient);
    }

    @Test
    void searchWithoutATermReturnsEveryPatient() {
        when(patientRepository.findAllByOrderByNameAsc()).thenReturn(List.of(new Patient()));

        assertThat(patientService.search("   ")).hasSize(1);
    }

    @Test
    void anUnknownPatientIdIsReportedAsNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
