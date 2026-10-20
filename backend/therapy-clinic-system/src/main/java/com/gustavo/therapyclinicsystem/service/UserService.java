package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.dto.user.CreateWorkspaceRequest;
import com.gustavo.therapyclinicsystem.dto.user.UserSummaryResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.EmailAlreadyExistsException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
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
    public User createUserWithWorkspace(
            CreateUserRequest userRequest,
            CreateWorkspaceRequest workspaceRequest
    ) {

        User user = createAndSaveUser(userRequest);

        Workspace workspace = createAndSaveWorkspace(
                user,
                workspaceRequest
        );

        createAndSaveMembership(
                user,
                workspace,
                WorkspaceRole.OWNER
        );

        return user;//TODO CHANGE TO SUMMARY
    }

    @Transactional
    public User createUserForExistingWorkspace(
            CreateUserRequest userRequest,
            Workspace workspace,
            WorkspaceRole role
    ) {

        User user = createAndSaveUser(userRequest);

        createAndSaveMembership(
                user,
                workspace,
                role
        );

        return user;//todo change to summary
    }

    private User createAndSaveUser(CreateUserRequest request) {
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setActive(true);

        return userRepository.save(user);
    }

    private Workspace createAndSaveWorkspace(User user, CreateWorkspaceRequest request) {
        Workspace workspace = new Workspace();

        String workspaceName;

        switch (request.plan()) {

            case TRIAL ->
                    workspaceName =
                            user.getFullName() + " Trial Workspace";

            case PERSONAL ->
                    workspaceName =
                            user.getFullName() + " Workspace";

            case CLINIC -> {
                if (request.workspaceName() == null ||
                        request.workspaceName().isBlank()) {

                    throw new BusinessRuleException(
                            "Workspace name is required for clinic plans"
                    );
                }

                workspaceName =
                        request.workspaceName().trim();
            }

            default ->
                    throw new BusinessRuleException(
                            "Invalid workspace plan"
                    );
        }

        workspace.setName(workspaceName);
        workspace.setPlan(request.plan());

        return workspaceRepository.save(workspace);
    }

    private void createAndSaveMembership(User user, Workspace workspace, WorkspaceRole role) {
        WorkspaceMembership membership =
                new WorkspaceMembership();

        membership.setUser(user);
        membership.setWorkspace(workspace);
        membership.setRole(role);

        workspaceMembershipRepository.save(membership);
    }





    //TODO forgot password
    /*public String forgotPassword (String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with email " + email + "not found"));
    }*/
}
