package com.gustavo.therapyclinicsystem.dto.session;

import com.gustavo.therapyclinicsystem.model.enums.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionOccurrenceSummaryResponse(
        UUID id,
        UUID patientId,
        UUID therapistId,
        UUID workspaceId,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        Integer feeCents,
        SessionStatus status
) {}