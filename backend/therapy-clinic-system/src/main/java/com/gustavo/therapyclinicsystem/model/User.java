package com.gustavo.therapyclinicsystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users",
uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = {"email"}))
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, length = 190)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.THERAPIST;

    // Trial limiter (optional)
    @Column(nullable = false)
    private Integer actionsRemaining = 20;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @OneToMany(mappedBy = "therapist", fetch = FetchType.LAZY)
    private List<Patient> patients = new ArrayList<>();

    public User(){}

    public User(Workspace workspace, String fullName, String email, String passwordHash, UserRole role) {
        this.workspace = workspace;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        patient.setTherapist(this);
    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
        patient.setTherapist(null);
    }


    public Instant getCreatedAt() {
        return createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getActionsRemaining() {
        return actionsRemaining;
    }

    public UserRole getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setActionsRemaining(Integer actionsRemaining) {
        this.actionsRemaining = actionsRemaining;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
