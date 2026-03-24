package com.gustavo.therapyclinicsystem.dto.workspace;

import com.gustavo.therapyclinicsystem.model.WorkspacePlan;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceResponse(
        UUID id,
        String name,
        String timezone,
        WorkspacePlan plan,
        Instant createdAt
) {
}
