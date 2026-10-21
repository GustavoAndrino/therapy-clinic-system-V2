package com.gustavo.therapyclinicsystem.dto.patient;

import java.util.UUID;

public record PatientClinicalDetailsResponse(
        UUID id,
        UUID workspaceId,
        UUID therapistId,
        String fullName,
        String cpf,
        String responsibleName,
        String responsibleCpf,
        String addressLine,
        String cep,
        String phoneNumber,
        String email,
        String adminObservations,
        Integer defaultSessionFeeCents,
        Integer paymentDayOfMonth,
        Boolean active,
        String therapistNotes
) implements PatientResponse {
}