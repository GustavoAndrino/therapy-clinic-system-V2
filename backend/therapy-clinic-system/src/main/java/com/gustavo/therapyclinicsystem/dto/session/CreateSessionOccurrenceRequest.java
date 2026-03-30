package com.gustavo.therapyclinicsystem.dto.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateSessionOccurrenceRequest(
        @NotNull UUID workspaceId,
        UUID scheduleId,
        @NotNull UUID patientId,
        @NotNull UUID therapistId,
        @NotNull OffsetDateTime startsAt,
        @NotNull OffsetDateTime endsAt,
        @NotNull @Min(0) Integer feeCents,
        String adminNotes
) {
}
