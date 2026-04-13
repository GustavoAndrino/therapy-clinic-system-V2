package com.gustavo.therapyclinicsystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace {

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

    @OneToMany(mappedBy = "workspace", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

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

    public List<User> getUsers() {
        return users;
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
}
