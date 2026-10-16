package com.gustavo.therapyclinicsystem.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public UUID getUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {

            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated user not found"
            );
        }

        return userDetails.getId();
    }
}

