package com.gustavo.therapyclinicsystem.dto.session;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionPaymentRequest(
        @NotNull Boolean paid
) {
}