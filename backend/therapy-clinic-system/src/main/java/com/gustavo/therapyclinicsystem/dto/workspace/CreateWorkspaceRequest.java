package com.gustavo.therapyclinicsystem.dto.workspace;

import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWorkspaceRequest(
        @NotBlank String name,
        String timezone,
        @NotNull WorkspacePlan plan
) {
}
