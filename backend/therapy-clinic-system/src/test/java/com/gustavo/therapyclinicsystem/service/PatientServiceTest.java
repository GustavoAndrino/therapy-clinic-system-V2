package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.patient.CreatePatientRequest;
import com.gustavo.therapyclinicsystem.dto.patient.PatientDetailsResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.Patient;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import com.gustavo.therapyclinicsystem.repository.PatientRepository;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
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

    @Mock
    private WorkspaceMembershipRepository workspaceMembershipRepository;

    @InjectMocks
    private PatientService patientService;

    private UUID workspaceId;
    private UUID requestingUserId;
    private UUID therapistId;

    private Workspace workspace;
    private User requestingUser;
    private User therapist;
    private WorkspaceMembership requestingMembership;
    private WorkspaceMembership therapistMembership;
    private CreatePatientRequest request;

    @BeforeEach
    void setUp() {
        workspaceId = UUID.randomUUID();
        requestingUserId = UUID.randomUUID();
        therapistId = UUID.randomUUID();

        workspace = new Workspace("Clinic A", WorkspacePlan.CLINIC);
        workspace.setId(workspaceId);

        requestingUser = buildUser(
                requestingUserId,
                "Clinic Owner",
                "owner@email.com"
        );

        therapist = buildUser(
                therapistId,
                "Dr. Gustavo",
                "gustavo@email.com"
        );

        requestingMembership = new WorkspaceMembership(
                requestingUser,
                workspace,
                WorkspaceRole.OWNER
        );

        therapistMembership = new WorkspaceMembership(
                therapist,
                workspace,
                WorkspaceRole.THERAPIST
        );

        request = buildRequest(therapistId);
    }

    @Test
    void createPatient_shouldCreatePatientSuccessfully() {
        mockValidRequestingMembership();

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapist));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                therapistId,
                workspaceId
        )).thenReturn(Optional.of(therapistMembership));

        UUID patientId = UUID.randomUUID();
        Patient savedPatient = buildSavedPatient(patientId, therapist);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(savedPatient);

        PatientDetailsResponse response = patientService.createPatient(request);

        assertNotNull(response);
        assertEquals(patientId, response.id());
        assertEquals("Maria Silva", response.fullName());
        assertEquals(workspaceId, response.workspaceId());
        assertEquals(therapistId, response.therapistId());
        assertEquals("maria@email.com", response.email());
        assertEquals(20000, response.defaultSessionFeeCents());
        assertEquals(10, response.paymentDayOfMonth());
        assertTrue(response.active());

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(Patient.class);

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
        assertEquals(
                "Observação administrativa",
                patientToSave.getAdminObservations()
        );
        assertEquals(20000, patientToSave.getDefaultSessionFeeCents());
        assertEquals(10, patientToSave.getPaymentDayOfMonth());
        assertTrue(patientToSave.getActive());

        verify(workspaceRepository).findById(workspaceId);
        verify(workspaceMembershipRepository)
                .findByUserIdAndWorkspaceId(
                        requestingUserId,
                        workspaceId
                );
        verify(userRepository).findById(therapistId);
        verify(workspaceMembershipRepository)
                .findByUserIdAndWorkspaceId(
                        therapistId,
                        workspaceId
                );
    }

    @Test
    void createPatient_shouldCreatePatientWithoutTherapistWhenTherapistIdIsNull() {
        CreatePatientRequest requestWithoutTherapist = buildRequest(null);

        mockValidRequestingMembership();

        UUID patientId = UUID.randomUUID();
        Patient savedPatient = buildSavedPatient(patientId, null);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(savedPatient);

        PatientDetailsResponse response =
                patientService.createPatient(requestWithoutTherapist);

        assertNotNull(response);
        assertEquals(patientId, response.id());
        assertNull(response.therapistId());

        ArgumentCaptor<Patient> patientCaptor =
                ArgumentCaptor.forClass(Patient.class);

        verify(patientRepository).save(patientCaptor.capture());

        Patient patientToSave = patientCaptor.getValue();
        assertEquals(workspace, patientToSave.getWorkspace());
        assertNull(patientToSave.getTherapist());

        verifyNoInteractions(userRepository);
        verify(workspaceMembershipRepository, never())
                .findByUserIdAndWorkspaceId(
                        therapistId,
                        workspaceId
                );
    }

    @Test
    void createPatient_shouldThrowWhenWorkspaceNotFound() {
        when(workspaceRepository.findById(workspaceId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "Workspace not found: " + workspaceId,
                ex.getMessage()
        );

        verify(workspaceRepository).findById(workspaceId);
        verifyNoInteractions(workspaceMembershipRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void createPatient_shouldThrowWhenRequestingUserDoesNotBelongToWorkspace() {
        when(workspaceRepository.findById(workspaceId))
                .thenReturn(Optional.of(workspace));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                requestingUserId,
                workspaceId
        )).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "Requesting user does not belong to the provided workspace",
                ex.getMessage()
        );

        verify(workspaceRepository).findById(workspaceId);
        verify(workspaceMembershipRepository)
                .findByUserIdAndWorkspaceId(
                        requestingUserId,
                        workspaceId
                );
        verifyNoInteractions(userRepository);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void createPatient_shouldThrowWhenRequestingUserCannotCreatePatients() {
        WorkspaceMembership therapistOnlyMembership = new WorkspaceMembership(
                requestingUser,
                workspace,
                WorkspaceRole.THERAPIST
        );

        when(workspaceRepository.findById(workspaceId))
                .thenReturn(Optional.of(workspace));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                requestingUserId,
                workspaceId
        )).thenReturn(Optional.of(therapistOnlyMembership));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "User is not allowed to create patients in this workspace",
                ex.getMessage()
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void createPatient_shouldThrowWhenTherapistNotFound() {
        mockValidRequestingMembership();

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "Therapist not found: " + therapistId,
                ex.getMessage()
        );

        verify(userRepository).findById(therapistId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_shouldThrowWhenTherapistDoesNotBelongToWorkspace() {
        mockValidRequestingMembership();

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapist));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                therapistId,
                workspaceId
        )).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "Therapist does not belong to the provided workspace",
                ex.getMessage()
        );

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_shouldThrowWhenAssignedUserCannotActAsTherapist() {
        WorkspaceMembership adminMembership = new WorkspaceMembership(
                therapist,
                workspace,
                WorkspaceRole.ADMIN
        );

        mockValidRequestingMembership();

        when(userRepository.findById(therapistId))
                .thenReturn(Optional.of(therapist));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                therapistId,
                workspaceId
        )).thenReturn(Optional.of(adminMembership));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> patientService.createPatient(request)
        );

        assertEquals(
                "Assigned user cannot act as a therapist in this workspace",
                ex.getMessage()
        );

        verify(patientRepository, never()).save(any(Patient.class));
    }

    private void mockValidRequestingMembership() {
        when(workspaceRepository.findById(workspaceId))
                .thenReturn(Optional.of(workspace));

        when(workspaceMembershipRepository.findByUserIdAndWorkspaceId(
                requestingUserId,
                workspaceId
        )).thenReturn(Optional.of(requestingMembership));
    }

    private CreatePatientRequest buildRequest(UUID assignedTherapistId) {
        return new CreatePatientRequest(
                workspaceId,
                requestingUserId,
                assignedTherapistId,
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

    private Patient buildSavedPatient(UUID patientId, User assignedTherapist) {
        Patient savedPatient = new Patient();
        savedPatient.setId(patientId);
        savedPatient.setWorkspace(workspace);
        savedPatient.setTherapist(assignedTherapist);
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
        return savedPatient;
    }

    private User buildUser(UUID id, String fullName, String email) {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        user.setActive(true);
        return user;
    }
}
