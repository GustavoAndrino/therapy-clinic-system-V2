package com.gustavo.therapyclinicsystem.dto.session;

import com.gustavo.therapyclinicsystem.model.SessionStatus;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionOccurrenceDetailsResponse(
        UUID id,
        UUID workspaceId,
        UUID scheduleId,
        UUID patientId,
        UUID therapistId,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        SessionStatus status,
        Boolean paid,
        Integer feeCents,
        String adminNotes,
        Instant createdAt
) {
}