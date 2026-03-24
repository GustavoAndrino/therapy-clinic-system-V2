package com.gustavo.therapyclinicsystem.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePatientRequest(
        @NotNull UUID workspaceId,
        @NotNull UUID therapistId,
        @NotBlank String fullName,
        String cpf,
        String responsibleName,
        String responsibleCpf,
        String addressLine,
        String cep,
        String phoneNumber,
        @Email String email,
        String adminObservations,
        @NotNull @Min(0) Integer defaultSessionFeeCents,
        @NotNull @Min(1) Integer paymentDayOfMonth,
        Boolean active
) {
}
