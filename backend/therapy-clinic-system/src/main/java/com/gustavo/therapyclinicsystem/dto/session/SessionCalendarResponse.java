package com.gustavo.therapyclinicsystem.dto.session;

import com.gustavo.therapyclinicsystem.model.enums.SessionStatus;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionCalendarResponse(
        UUID sessionId,
        UUID patientId,
        String patientName,
        UUID workspaceId,
        String workspaceName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        SessionStatus status
) {
}
