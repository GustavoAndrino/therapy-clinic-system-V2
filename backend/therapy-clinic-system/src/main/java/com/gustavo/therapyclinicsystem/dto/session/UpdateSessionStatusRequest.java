package com.gustavo.therapyclinicsystem.dto.session;

import com.gustavo.therapyclinicsystem.model.SessionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSessionStatusRequest(
        @NotNull SessionStatus status
) {
}