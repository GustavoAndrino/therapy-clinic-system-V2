package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.LoginRequest;
import com.gustavo.therapyclinicsystem.dto.user.LoginResponse;
import com.gustavo.therapyclinicsystem.dto.workspace.WorkspaceMembershipResponse;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import com.gustavo.therapyclinicsystem.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            WorkspaceMembershipRepository workspaceMembershipRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.workspaceMembershipRepository = workspaceMembershipRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(
                userDetails.getId()
        );

        List<WorkspaceMembership> memberships =
                workspaceMembershipRepository.findByUserId(userDetails.getId());

        List<WorkspaceMembershipResponse> workspaces =
                memberships.stream()
                        .map(membership ->
                                new WorkspaceMembershipResponse(
                                        membership.getWorkspace().getId(),
                                        membership.getWorkspace().getName(),
                                        membership.getRole()
                                )
                        )
                        .toList();

        //The first thing user should see after loing its the workspaces they belong, thats why we add them here in auth
        //It avoids having to do a second api call right after the login one
        return new LoginResponse(
                token,
                workspaces
        );

    }
}