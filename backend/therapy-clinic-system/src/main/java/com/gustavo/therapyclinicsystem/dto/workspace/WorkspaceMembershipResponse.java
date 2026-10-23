package com.gustavo.therapyclinicsystem.dto.workspace;

import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;

import java.util.UUID;

public record WorkspaceMembershipResponse(
        UUID workspaceId,
        String workspaceName,
        WorkspaceRole role
) {
}
