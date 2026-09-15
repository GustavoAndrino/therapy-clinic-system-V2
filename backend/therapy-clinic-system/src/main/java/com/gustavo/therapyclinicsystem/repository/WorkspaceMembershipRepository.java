package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMembershipRepository
        extends JpaRepository<WorkspaceMembership, UUID> {

    List<WorkspaceMembership> findByUserId(UUID userId);

    List<WorkspaceMembership> findByWorkspaceId(UUID workspaceId);

    Optional<WorkspaceMembership> findByUserIdAndWorkspaceId(
            UUID userId,
            UUID workspaceId
    );

    boolean existsByUserIdAndWorkspaceId(
            UUID userId,
            UUID workspaceId
    );

    boolean existsByUserIdAndWorkspaceIdAndRole(
            UUID userId,
            UUID workspaceId,
            WorkspaceRole role
    );
}