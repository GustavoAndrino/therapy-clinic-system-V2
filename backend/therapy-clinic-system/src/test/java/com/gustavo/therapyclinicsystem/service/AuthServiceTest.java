package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.LoginRequest;
import com.gustavo.therapyclinicsystem.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() {

        UUID userId = UUID.randomUUID();

        LoginRequest request = new LoginRequest(
                "gustavo@email.com",
                "Password123"
        );

        Authentication authenticatedResult =
                mock(Authentication.class);

        CustomUserDetails userDetails =
                mock(CustomUserDetails.class);

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenReturn(authenticatedResult);

        when(authenticatedResult.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getId())
                .thenReturn(userId);

        when(jwtService.generateToken(userId))
                .thenReturn("fake-jwt-token");

        String token = authService.login(request);

        assertEquals("fake-jwt-token", token);

        ArgumentCaptor<Authentication> authenticationCaptor =
                ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager)
                .authenticate(authenticationCaptor.capture());

        Authentication authenticationRequest =
                authenticationCaptor.getValue();

        assertEquals(
                "gustavo@email.com",
                authenticationRequest.getPrincipal()
        );

        assertEquals(
                "Password123",
                authenticationRequest.getCredentials()
        );

        verify(jwtService)
                .generateToken(userId);
    }

    @Test
    void login_shouldThrowWhenCredentialsAreInvalid() {

        LoginRequest request = new LoginRequest(
                "gustavo@email.com",
                "wrong-password"
        );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenThrow(
                new BadCredentialsException("Bad credentials")
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager)
                .authenticate(any(Authentication.class));

        verifyNoInteractions(jwtService);
    }
}