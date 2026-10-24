package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.dto.user.CreateWorkspaceRequest;
import com.gustavo.therapyclinicsystem.dto.user.UserSummaryResponse;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignUpService {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final WorkspaceMembershipService workspaceMembershipService;

    public SignUpService(UserService userService, WorkspaceService workspaceService, WorkspaceMembershipService workspaceMembershipService) {
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.workspaceMembershipService = workspaceMembershipService;
    }

    @Transactional
    public UserSummaryResponse createUserAndWorkspace(
            CreateUserRequest userRequest
    ) {

        User user = userService.createAndSaveUser(userRequest);

        Workspace workspace = workspaceService.createTrialWorkspace(user);

        workspaceMembershipService.createAndSaveMembership(
                user,
                workspace,
                WorkspaceRole.OWNER
        );

        return toSummaryResponse(user);
    }

    //The method below is for future invitations functionality
    @Transactional
    public UserSummaryResponse createUserForExistingWorkspace(
            CreateUserRequest userRequest,
            Workspace workspace,
            WorkspaceRole role
    ) {

        //todo Check if user already exists before creating it
        User user = userService.createAndSaveUser(userRequest);

        workspaceMembershipService.createAndSaveMembership(
                user,
                workspace,
                role
        );

        return toSummaryResponse(user);
    }

    private UserSummaryResponse toSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getActive()
        );
    }
}
