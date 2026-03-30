package com.gustavo.therapyclinicsystem.dto.note;

import java.time.Instant;
import java.util.UUID;

public record ClinicalNoteResponse(
        UUID id,
        UUID workspaceId,
        UUID patientId,
        UUID therapistId,
        UUID sessionOccurrenceId,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}