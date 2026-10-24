package com.gustavo.therapyclinicsystem.controller;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.dto.user.CreateWorkspaceRequest;
import com.gustavo.therapyclinicsystem.dto.user.UserSummaryResponse;
import com.gustavo.therapyclinicsystem.service.SignUpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/signup")
public class SignUpController {

    private final SignUpService signUpService;

    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSummaryResponse createUserAndWorkspace (
           @Valid @RequestBody CreateUserRequest createUserRequest
    ) {
        return signUpService.createUserAndWorkspace(createUserRequest);
    }

    //TODO This will be created after invitations is built --> public UserSummaryResponse createUserForExistingWorkspace (){}
}
