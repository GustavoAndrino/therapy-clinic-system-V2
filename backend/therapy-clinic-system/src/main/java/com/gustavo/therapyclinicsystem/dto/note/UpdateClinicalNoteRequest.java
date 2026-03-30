package com.gustavo.therapyclinicsystem.dto.note;

import jakarta.validation.constraints.NotBlank;

public record UpdateClinicalNoteRequest(
        @NotBlank String content
) {
}