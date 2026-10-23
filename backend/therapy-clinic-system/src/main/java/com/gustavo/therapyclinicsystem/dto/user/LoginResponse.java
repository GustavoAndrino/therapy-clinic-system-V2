package com.gustavo.therapyclinicsystem.dto.user;

import com.gustavo.therapyclinicsystem.dto.workspace.WorkspaceMembershipResponse;

import java.util.List;

public record LoginResponse(
        String token,
        List<WorkspaceMembershipResponse> workspaces
) {
}