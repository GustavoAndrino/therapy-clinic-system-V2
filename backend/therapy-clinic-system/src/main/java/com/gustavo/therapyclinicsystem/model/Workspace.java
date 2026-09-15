package com.gustavo.therapyclinicsystem.model;

import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace {

    //TODO: add billing inforamtion in workspace

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 60)
    private String timezone = "America/Sao_Paulo";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspacePlan plan = WorkspacePlan.PERSONAL;

    // Trial usage belongs to the billable workspace, not to an individual user.
    @Column(nullable = false)
    private Integer actionsRemaining = 20;

    @OneToMany(
            mappedBy = "workspace",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkspaceMembership> memberships =
            new ArrayList<>();

    public Workspace() {}

    public Workspace(String name, WorkspacePlan plan) {
        this.name = name;
        this.plan = plan;
    }

    public static Workspace createTrial(String name) {
        return new Workspace(name, WorkspacePlan.TRIAL);
    }

    public static Workspace createPersonal(String therapistDisplayName) {
        return new Workspace(therapistDisplayName, WorkspacePlan.PERSONAL);
    }

    public static Workspace createClinic(String clinicName) {
        return new Workspace(clinicName, WorkspacePlan.CLINIC);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTimezone() {
        return timezone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public WorkspacePlan getPlan() {
        return plan;
    }

    public Integer getActionsRemaining() {
        return actionsRemaining;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setPlan(WorkspacePlan plan) {
        this.plan = plan;
    }

    public void setActionsRemaining(Integer actionsRemaining) {
        this.actionsRemaining = actionsRemaining;
    }

    public List<WorkspaceMembership> getMemberships() {
        return memberships;
    }

    public void addMembership(
            WorkspaceMembership membership
    ) {
        memberships.add(membership);
        membership.setWorkspace(this);
    }

    public void removeMembership(
            WorkspaceMembership membership
    ) {
        memberships.remove(membership);
        membership.setWorkspace(null);
    }
}
