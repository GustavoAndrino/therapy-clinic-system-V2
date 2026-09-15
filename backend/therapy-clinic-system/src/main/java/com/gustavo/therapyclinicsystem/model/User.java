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

    @Column(nullable = false, length = 190, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkspaceMembership> workspaceMemberships =
            new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public User(){}

    public User(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }


    public Instant getCreatedAt() {
        return createdAt;
    }

    public Boolean getActive() {
        return active;
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

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<WorkspaceMembership> getWorkspaceMemberships() {
        return workspaceMemberships;
    }

    public void addWorkspaceMembership(
            WorkspaceMembership membership
    ) {
        workspaceMemberships.add(membership);
        membership.setUser(this);
    }

    public void removeWorkspaceMembership(
            WorkspaceMembership membership
    ) {
        workspaceMemberships.remove(membership);
        membership.setUser(null);
    }
}


