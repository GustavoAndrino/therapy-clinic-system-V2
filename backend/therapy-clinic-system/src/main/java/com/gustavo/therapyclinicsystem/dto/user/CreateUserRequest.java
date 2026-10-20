package com.gustavo.therapyclinicsystem.dto.user;

import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String password
        ) {
}
