package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.workspace.WorkspaceMembershipResponse;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceMembershipService {

    private final WorkspaceMembershipRepository workspaceMembershipRepository;

    public WorkspaceMembershipService(WorkspaceMembershipRepository workspaceMembershipRepository) {
        this.workspaceMembershipRepository = workspaceMembershipRepository;
    }

    public void createAndSaveMembership(User user, Workspace workspace, WorkspaceRole role) {
        WorkspaceMembership membership =
                new WorkspaceMembership();

        membership.setUser(user);
        membership.setWorkspace(workspace);
        membership.setRole(role);

        workspaceMembershipRepository.save(membership);
    }

    // todo public WorkspaceMembershipResponse updateWorkspaceMembershipRole()




}
