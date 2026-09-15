package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.patient.CreatePatientRequest;
import com.gustavo.therapyclinicsystem.dto.patient.PatientDetailsResponse;
import com.gustavo.therapyclinicsystem.dto.patient.PatientSummaryResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.Patient;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.repository.PatientRepository;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;

    public PatientService(
            PatientRepository patientRepository,
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository,
            WorkspaceMembershipRepository workspaceMembershipRepository
    ) {
        this.patientRepository = patientRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.workspaceMembershipRepository = workspaceMembershipRepository;
    }

    public PatientDetailsResponse createPatient(CreatePatientRequest request) {
        Workspace workspace = workspaceRepository.findById(request.workspaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workspace not found: " + request.workspaceId()
                        )
                );

        WorkspaceMembership requestingMembership = workspaceMembershipRepository
                .findByUserIdAndWorkspaceId(request.userId(), workspace.getId())
                .orElseThrow(() ->
                        new BusinessRuleException(
                                "Requesting user does not belong to the provided workspace"
                        )
                );

        validateCanCreatePatient(requestingMembership);

        User therapist = null;

        if (request.therapistId() != null) {
            therapist = userRepository.findById(request.therapistId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Therapist not found: " + request.therapistId()
                            )
                    );

            WorkspaceMembership therapistMembership = workspaceMembershipRepository
                    .findByUserIdAndWorkspaceId(therapist.getId(), workspace.getId())
                    .orElseThrow(() ->
                            new BusinessRuleException(
                                    "Therapist does not belong to the provided workspace"
                            )
                    );

            validateCanBeAssignedAsTherapist(therapistMembership);
        }

        Patient patient = buildPatient(request, workspace, therapist);
        Patient savedPatient = patientRepository.save(patient);

        return toDetailsResponse(savedPatient);
    }

    @Transactional(readOnly = true)
    public PatientDetailsResponse getPatientById(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found: " + patientId
                        )
                );

        return toDetailsResponse(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> getPatientsByWorkspace(UUID workspaceId) {
        return patientRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryResponse> getPatientsByTherapist(UUID therapistId) {
        return patientRepository.findByTherapistId(therapistId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    private void validateCanCreatePatient(WorkspaceMembership membership) {
        if (!membership.getRole().canCreatePatients()) {
            throw new BusinessRuleException(
                    "User is not allowed to create patients in this workspace"
            );
        }
    }

    private void validateCanBeAssignedAsTherapist(WorkspaceMembership membership) {
        if (!membership.getRole().canBeAssignedPatients()) {
            throw new BusinessRuleException(
                    "Assigned user cannot act as a therapist in this workspace"
            );
        }
    }

    private PatientSummaryResponse toSummaryResponse(Patient patient) {
        return new PatientSummaryResponse(
                patient.getId(),
                patient.getFullName(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getPaymentDayOfMonth(),
                patient.getActive()
        );
    }

    private PatientDetailsResponse toDetailsResponse(Patient patient) {
        return new PatientDetailsResponse(
                patient.getId(),
                patient.getWorkspace().getId(),
                patient.getTherapist() != null
                        ? patient.getTherapist().getId()
                        : null,
                patient.getFullName(),
                patient.getCpf(),
                patient.getResponsibleName(),
                patient.getResponsibleCpf(),
                patient.getAddressLine(),
                patient.getCep(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAdminObservations(),
                patient.getDefaultSessionFeeCents(),
                patient.getPaymentDayOfMonth(),
                patient.getActive()
        );
    }

    private Patient buildPatient(
            CreatePatientRequest request,
            Workspace workspace,
            User therapist
    ) {
        Patient patient = new Patient();
        patient.setWorkspace(workspace);
        patient.setTherapist(therapist);
        patient.setFullName(request.fullName());
        patient.setEmail(request.email());
        patient.setCpf(request.cpf());
        patient.setCep(request.cep());
        patient.setResponsibleCpf(request.responsibleCpf());
        patient.setResponsibleName(request.responsibleName());
        patient.setAddressLine(request.addressLine());
        patient.setPhoneNumber(request.phoneNumber());
        patient.setAdminObservations(request.adminObservations());
        patient.setDefaultSessionFeeCents(request.defaultSessionFeeCents());
        patient.setPaymentDayOfMonth(request.paymentDayOfMonth());

        if (request.active() != null) {
            patient.setActive(request.active());
        }

        return patient;
    }
}
