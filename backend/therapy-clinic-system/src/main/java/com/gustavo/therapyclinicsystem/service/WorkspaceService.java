package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateWorkspaceRequest;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Workspace createTrialWorkspace(User user){
        Workspace workspace = new Workspace();

        String workspaceName = user.getFullName() + " Trial Workspace";

        workspace.setName(workspaceName);
        workspace.setPlan(WorkspacePlan.TRIAL);

        return workspaceRepository.save(workspace);

    }

    public Workspace createPaidWorkspace(CreateWorkspaceRequest request) {
        Workspace workspace = new Workspace();

        if (request.plan() == null || request.plan() == WorkspacePlan.TRIAL) {
            throw new BusinessRuleException(
                    "Only paid workspace plans are accepted"
            );
        }
        if (request.workspaceName() == null ||
                request.workspaceName().isBlank()) {

            throw new BusinessRuleException(
                    "Workspace name is required"
            );

            workspace.setName(
                    request.workspaceName().trim()
            );

        workspace.setName(request.workspaceName());
        workspace.setPlan(request.plan());

        return workspaceRepository.save(workspace);
    }
}
