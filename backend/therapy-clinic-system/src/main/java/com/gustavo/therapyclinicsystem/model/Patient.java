package com.gustavo.therapyclinicsystem.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "patients",
        indexes = {
                @Index(name = "idx_patients_workspace", columnList = "workspace_id"),
                @Index(name = "idx_patients_email", columnList = "email"),
                @Index(name= "idx_patients_therapist", columnList = "therapist_id")
        })
public class Patient {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    @Column(nullable = false, length = 160)
    private String fullName;

    // CHECK IF REALLY NECESSARY!!
    @Column(length = 14)
    private String cpf;

    @Column(length = 160)
    private String responsibleName;

    @Column(length = 14)
    private String responsibleCpf;

    @Column(length = 220)
    private String addressLine;

    @Column(length = 12)
    private String cep;

    @Column(length = 30)
    private String phoneNumber;

    @Column(length = 190)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adminObservations;

    @Column(nullable = false)
    private Integer defaultSessionFeeCents;

    @Column(nullable = false)
    private Integer paymentDayOfMonth;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Patient() {
    }

    public Patient(Workspace workspace, String fullName, Integer defaultSessionFeeCents, Integer paymentDayOfMonth) {
        this.workspace = workspace;
        this.fullName = fullName;
        this.defaultSessionFeeCents = defaultSessionFeeCents;
        this.paymentDayOfMonth = paymentDayOfMonth;
    }

    public UUID getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public User getTherapist() {
        return therapist;
    }

    public void setTherapist(User therapist) {
        this.therapist = therapist;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public String getResponsibleCpf() {
        return responsibleCpf;
    }

    public void setResponsibleCpf(String responsibleCpf) {
        this.responsibleCpf = responsibleCpf;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminObservations() {
        return adminObservations;
    }

    public void setAdminObservations(String adminObservations) {
        this.adminObservations = adminObservations;
    }

    public Integer getDefaultSessionFeeCents() {
        return defaultSessionFeeCents;
    }

    public void setDefaultSessionFeeCents(Integer defaultSessionFeeCents) {
        this.defaultSessionFeeCents = defaultSessionFeeCents;
    }
}
