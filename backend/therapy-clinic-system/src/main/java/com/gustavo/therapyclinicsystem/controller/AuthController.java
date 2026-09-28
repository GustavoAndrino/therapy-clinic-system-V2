package com.gustavo.therapyclinicsystem.controller;

import com.gustavo.therapyclinicsystem.dto.user.LoginRequest;
import com.gustavo.therapyclinicsystem.dto.user.LoginResponse;
import com.gustavo.therapyclinicsystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {

        String token = authService.login(request);

        return new LoginResponse(token);
    }
}