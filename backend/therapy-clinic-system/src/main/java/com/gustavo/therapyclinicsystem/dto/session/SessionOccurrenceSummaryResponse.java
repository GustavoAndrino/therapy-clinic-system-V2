package com.gustavo.therapyclinicsystem.dto.session;

import com.gustavo.therapyclinicsystem.model.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionOccurrenceSummaryResponse(
        UUID id,
        UUID patientId,
        String patientName,
        UUID therapistId,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        SessionStatus status,
        Boolean paid,
        Integer feeCents
) {
}