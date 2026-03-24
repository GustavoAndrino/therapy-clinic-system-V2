package com.gustavo.therapyclinicsystem.dto.patient;

import java.util.UUID;

public record PatientSummaryResponse(
        UUID id,
        String fullName,
        String phoneNumber,
        String email,
        Integer paymentDayOfMonth,
        Boolean active
) {
}
