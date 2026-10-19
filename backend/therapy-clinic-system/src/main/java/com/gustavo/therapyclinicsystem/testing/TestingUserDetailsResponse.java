package com.gustavo.therapyclinicsystem.testing;

import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;

import java.util.List;
import java.util.UUID;

public record TestingUserDetailsResponse(
        UUID userId,
        String fullName,
        String email,
        String passwordHash,
        boolean active,
        List<MembershipInfo> memberships
) {

    public record MembershipInfo(
            UUID membershipId,
            WorkspaceRole role,
            UUID workspaceId,
            String workspaceName,
            WorkspacePlan workspacePlan
    ) {
    }
}