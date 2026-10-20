package com.gustavo.therapyclinicsystem.dto.user;

import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import jakarta.validation.constraints.NotNull;

public record CreateWorkspaceRequest(
            @NotNull WorkspacePlan plan,
            String workspaceName
) {
}
