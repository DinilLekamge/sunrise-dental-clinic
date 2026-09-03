package com.sunrise.clinic.controller;

import com.sunrise.clinic.config.SecurityConfig;
import com.sunrise.clinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Checks that the Spring Security rules really stop a user from opening a page
 * that their role is not allowed to use.
 */
@WebMvcTest(controllers = PatientController.class)
@Import(SecurityConfig.class)
class PatientAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void anAnonymousVisitorIsSentToTheLoginPage() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "reception", roles = "RECEPTIONIST")
    void aReceptionistCanOpenTheRegisterPatientForm() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "dentist", roles = "DENTIST")
    void aDentistCannotOpenTheRegisterPatientForm() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "dentist", roles = "DENTIST")
    void aDentistCanSearchPatients() throws Exception {
        when(patientService.search(null)).thenReturn(List.of());

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void anAdministratorCannotOpenThePatientPages() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isForbidden());
    }
}
