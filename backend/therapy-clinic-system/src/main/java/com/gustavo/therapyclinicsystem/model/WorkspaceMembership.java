package com.gustavo.therapyclinicsystem.model;

import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "workspace_memberships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_membership_user_workspace",
                        columnNames = {"user_id", "workspace_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_membership_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_membership_workspace",
                        columnList = "workspace_id"
                )
        }
)
public class WorkspaceMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "workspace_id",
            nullable = false
    )
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkspaceRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public WorkspaceMembership() {
    }

    public WorkspaceMembership(
            User user,
            Workspace workspace,
            WorkspaceRole role
    ) {
        this.user = user;
        this.workspace = workspace;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public WorkspaceRole getRole() {
        return role;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}