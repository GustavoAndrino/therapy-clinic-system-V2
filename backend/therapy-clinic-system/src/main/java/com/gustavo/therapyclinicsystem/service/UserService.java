package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.dto.user.UserSummaryResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.EmailAlreadyExistsException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceType;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;

    public UserService(
            UserRepository userRepository, WorkspaceRepository workspaceRepository,
            PasswordEncoder passwordEncoder, WorkspaceMembershipRepository workspaceMembershipRepository
    ) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.passwordEncoder = passwordEncoder;
        this.workspaceMembershipRepository = workspaceMembershipRepository;
    }


    @Transactional
    public UserSummaryResponse createUser(CreateUserRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.fullName());
        user.setEmail(email);

        String passwordHash =
                passwordEncoder.encode(request.password());

        user.setPasswordHash(passwordHash);
        user.setActive(true); //TODO Leave true for testing, set false later
        User savedUser = userRepository.save(user);


        Workspace workspace = getWorkspace(request);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setUser(savedUser);
        membership.setWorkspace(savedWorkspace);
        membership.setRole(WorkspaceRole.OWNER);

        workspaceMembershipRepository.save(membership);



        return new UserSummaryResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getActive()
        );
    }

    private static @NonNull Workspace getWorkspace(CreateUserRequest request) {
        String workspaceName;

        if (request.workspaceType() == WorkspaceType.INDIVIDUAL) {

            workspaceName = request.fullName().trim();

        } else {

            if (request.workspaceName() == null ||
                    request.workspaceName().isBlank()) {

                throw new BusinessRuleException(
                        "Workspace name is required for clinic accounts"
                );
            }

            workspaceName = request.workspaceName().trim();
        }

        Workspace workspace = new Workspace();

        workspace.setName(workspaceName);
        workspace.setPlan(WorkspacePlan.TRIAL);
        return workspace;
    }


    //TODO forgot password
    /*public String forgotPassword (String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with email " + email + "not found"));
    }*/
}
