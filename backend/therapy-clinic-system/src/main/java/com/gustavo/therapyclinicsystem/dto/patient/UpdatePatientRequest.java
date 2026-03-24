package com.gustavo.therapyclinicsystem.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdatePatientRequest(
        @NotBlank String fullName,
        String cpf,
        String responsibleName,
        String responsibleCpf,
        String addressLine,
        String cep,
        String phoneNumber,
        @Email String email,
        String adminObservations,
        @Min(0) Integer defaultSessionFeeCents,
        @Min(1) Integer paymentDayOfMonth,
        Boolean active
) {
}
