package com.gustavo.therapyclinicsystem.controller;

import com.gustavo.therapyclinicsystem.config.SecurityConfig;
import tools.jackson.databind.json.JsonMapper;
import com.gustavo.therapyclinicsystem.dto.patient.CreatePatientRequest;
import com.gustavo.therapyclinicsystem.dto.patient.PatientDetailsResponse;
import com.gustavo.therapyclinicsystem.dto.patient.PatientSummaryResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.GlobalExceptionHandler;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfig.class
})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private PatientService patientService;

    @Test
    void createPatient_shouldReturnCreated() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        CreatePatientRequest request = new CreatePatientRequest(
                workspaceId,
                therapistId,
                "Maria Silva",
                "12345678900",
                null,
                null,
                "Rua A, 123",
                "70000-000",
                "61999999999",
                "maria@email.com",
                null,
                20000,
                10,
                true
        );

        PatientDetailsResponse response = new PatientDetailsResponse(
                patientId,
                workspaceId,
                therapistId,
                "Maria Silva",
                "12345678900",
                null,
                null,
                "Rua A, 123",
                "70000-000",
                "61999999999",
                "maria@email.com",
                null,
                20000,
                10,
                true
        );

        when(patientService.createPatient(any(CreatePatientRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.workspaceId").value(workspaceId.toString()))
                .andExpect(jsonPath("$.therapistId").value(therapistId.toString()))
                .andExpect(jsonPath("$.fullName").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.defaultSessionFeeCents").value(20000))
                .andExpect(jsonPath("$.paymentDayOfMonth").value(10))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createPatient_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();

        String invalidJson = """
                {
                    "workspaceId": "%s",
                    "therapistId": "%s",
                    "fullName": "",
                    "email": "not-an-email",
                    "defaultSessionFeeCents": -100,
                    "paymentDayOfMonth": 0
                }
                """.formatted(workspaceId, therapistId);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getPatientById_shouldReturnPatient() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();

        PatientDetailsResponse response = new PatientDetailsResponse(
                patientId,
                workspaceId,
                therapistId,
                "Maria Silva",
                null,
                null,
                null,
                null,
                null,
                "61999999999",
                "maria@email.com",
                null,
                20000,
                10,
                true
        );

        when(patientService.getPatientById(patientId))
                .thenReturn(response);

        mockMvc.perform(get("/patients/{patientId}", patientId))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.fullName").value("Maria Silva"));
    }

    @Test
    void getPatientById_shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        UUID patientId = UUID.randomUUID();

        when(patientService.getPatientById(patientId))
                .thenThrow(new ResourceNotFoundException(
                        "Patient not found: " + patientId
                ));

        mockMvc.perform(get("/patients/{patientId}", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found: " + patientId));
    }

    @Test
    void createPatient_shouldReturnBadRequestForBusinessRuleViolation() throws Exception {
        UUID workspaceId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();

        CreatePatientRequest request = new CreatePatientRequest(
                workspaceId,
                therapistId,
                "Maria Silva",
                null,
                null,
                null,
                null,
                null,
                null,
                "maria@email.com",
                null,
                20000,
                10,
                true
        );

        when(patientService.createPatient(any(CreatePatientRequest.class)))
                .thenThrow(new BusinessRuleException(
                        "Therapist does not belong to the provided workspace"
                ));

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Therapist does not belong to the provided workspace"));
    }

    @Test
    void getPatientsByTherapist_shouldReturnPatientSummaries() throws Exception {
        UUID therapistId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        List<PatientSummaryResponse> response = List.of(
                new PatientSummaryResponse(
                        patientId,
                        "Maria Silva",
                        "61999999999",
                        "maria@email.com",
                        10,
                        true
                )
        );

        when(patientService.getPatientsByTherapist(therapistId))
                .thenReturn(response);

        mockMvc.perform(get("/patients/therapist/{therapistId}", therapistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(patientId.toString()))
                .andExpect(jsonPath("$[0].fullName").value("Maria Silva"));
    }
}