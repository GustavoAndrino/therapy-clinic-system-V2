package com.gustavo.therapyclinicsystem.dto.user;

import com.gustavo.therapyclinicsystem.model.UserRole;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        UUID workspaceId,
        String fullName,
        String email,
        UserRole role,
        Boolean active
) {
}
