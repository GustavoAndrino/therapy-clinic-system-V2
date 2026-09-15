package com.gustavo.therapyclinicsystem.dto.user;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String fullName,
        String email,
        Boolean active
) {
}
