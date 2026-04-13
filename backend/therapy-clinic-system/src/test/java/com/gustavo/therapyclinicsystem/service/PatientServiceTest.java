package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.patient.CreatePatientRequest;
import com.gustavo.therapyclinicsystem.dto.patient.PatientDetailsResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.Patient;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.UserRole;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspacePlan;
import com.gustavo.therapyclinicsystem.repository.PatientRepository;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    private UUID workspaceId;
    private UUID therapistId;
    private Workspace workspace;
    private User therapist;
    private CreatePatientRequest request;

    @BeforeEach
    void setUp() {
        workspaceId = UUID.randomUUID();
        therapistId = UUID.randomUUID();

        workspace = new Workspace("Clinic A", WorkspacePlan.PERSONAL);
        workspace.setId(workspaceId);

        therapist = new User();
        therapist.setId(therapistId);
        therapist.setFullName("Dr. Gustavo");
        therapist.setEmail("gustavo@email.com");
        therapist.setPasswordHash("hashed-password");
        therapist.setRole(UserRole.THERAPIST);
        therapist.setActive(true);
        therapist.setWorkspace(workspace);

        request = new CreatePatientRequest(
                workspaceId,
                therapistId,
                "Maria Silva",
                "12345678900",
                "João Silva",
                "98765432100",
                "Rua A, 123",
                "70000-000",
                "61999999999",
                "maria@email.com",
                "Observação administrativa",
                20000,
                10,
                true
        );
    }

    @Test
    void createPatient_shouldCreatePatientSuccessfully() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));

        Patient savedPatient = new Patient();
        savedPatient.setId(UUID.randomUUID());
        savedPatient.setWorkspace(workspace);
        savedPatient.setTherapist(therapist);
        savedPatient.setFullName(request.fullName());
        savedPatient.setCpf(request.cpf());
        savedPatient.setResponsibleName(request.responsibleName());
        savedPatient.setResponsibleCpf(request.responsibleCpf());
        savedPatient.setAddressLine(request.addressLine());
        savedPatient.setCep(request.cep());
        savedPatient.setPhoneNumber(request.phoneNumber());
        savedPatient.setEmail(request.email());
        savedPatient.setAdminObservations(request.adminObservations());
        savedPatient.setDefaultSessionFeeCents(request.defaultSessionFeeCents());
        savedPatient.setPaymentDayOfMonth(request.paymentDayOfMonth());
        savedPatient.setActive(true);

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientDetailsResponse response = patientService.createPatient(request);

        assertNotNull(response);
        assertEquals(savedPatient.getId(), response.id());
        assertEquals("Maria Silva", response.fullName());
        assertEquals(workspaceId, response.workspaceId());
        assertEquals(therapistId, response.therapistId());
        assertEquals("maria@email.com", response.email());
        assertEquals(20000, response.defaultSessionFeeCents());
        assertEquals(10, response.paymentDayOfMonth());
        assertTrue(response.active());

        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());

        Patient patientToSave = patientCaptor.getValue();
        assertEquals(workspace, patientToSave.getWorkspace());
        assertEquals(therapist, patientToSave.getTherapist());
        assertEquals("Maria Silva", patientToSave.getFullName());
        assertEquals("12345678900", patientToSave.getCpf());
        assertEquals("João Silva", patientToSave.getResponsibleName());
        assertEquals("98765432100", patientToSave.getResponsibleCpf());
        assertEquals("Rua A, 123", patientToSave.getAddressLine());
        assertEquals("70000-000", patientToSave.getCep());
        assertEquals("61999999999", patientToSave.getPhoneNumber());
        assertEquals("maria@email.com", patientToSave.getEmail());
        assertEquals("Observação administrativa", patientToSave.getAdminObservations());
        assertEquals(20000, patientToSave.getDefaultSessionFeeCents());
        assertEquals(10, patientToSave.getPaymentDayOfMonth());
        assertTrue(patientToSave.getActive());

        verify(workspaceRepository).findById(workspaceId);
        verify(userRepository).findById(therapistId);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_shouldThrowWhenWorkspaceNotFound() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals("Workspace not found: " + workspaceId, ex.getMessage());

        verify(workspaceRepository).findById(workspaceId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void createPatient_shouldThrowWhenTherapistNotFound() {
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
        when(userRepository.findById(therapistId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals("Therapist not found: " + therapistId, ex.getMessage());

        verify(workspaceRepository).findById(workspaceId);
        verify(userRepository).findById(therapistId);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void createPatient_shouldThrowWhenTherapistBelongsToAnotherWorkspace() {
        Workspace anotherWorkspace = new Workspace("Clinic B", WorkspacePlan.CLINIC);
        anotherWorkspace.setId(UUID.randomUUID());

        therapist.setWorkspace(anotherWorkspace);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals("Therapist does not belong to the provided workspace", ex.getMessage());

        verify(workspaceRepository).findById(workspaceId);
        verify(userRepository).findById(therapistId);
        verifyNoInteractions(patientRepository);
    }
}