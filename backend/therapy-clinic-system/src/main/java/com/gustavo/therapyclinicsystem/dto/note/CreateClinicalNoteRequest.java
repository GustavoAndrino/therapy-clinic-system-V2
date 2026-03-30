package com.gustavo.therapyclinicsystem.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateClinicalNoteRequest(
        @NotNull UUID workspaceId,
        @NotNull UUID patientId,
        @NotNull UUID therapistId,
        UUID sessionOccurrenceId,
        @NotBlank String content
) {
}