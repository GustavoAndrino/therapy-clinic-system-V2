package com.gustavo.therapyclinicsystem.dto.user;

import com.gustavo.therapyclinicsystem.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserRequest(
        @NotNull UUID workspaceId,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull UserRole role
) {
}
