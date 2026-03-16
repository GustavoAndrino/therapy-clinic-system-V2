package com.gustavo.therapyclinicsystem.model;

import com.gustavo.therapyclinicsystem.model.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_occurrences",
        indexes = {
                @Index(name = "idx_session_workspace", columnList = "workspace_id"),
                @Index(name = "idx_session_patient", columnList = "patient_id"),
                @Index(name = "idx_session_therapist", columnList = "therapist_id"),
                @Index(name = "idx_session_starts_at", columnList = "starts_at")
        })
public class SessionOccurrence {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private RecurringSchedule schedule;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(nullable = false)
    private Boolean paid = false;

    // Snapshot fee at the time the session was created
    @Column(nullable = false)
    private Integer feeCents;

    // Non-clinical note about the session (e.g., "rescheduled")
    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected SessionOccurrence() {}

    public SessionOccurrence(Workspace workspace, RecurringSchedule schedule, Patient patient, User therapist,
                             OffsetDateTime startsAt, OffsetDateTime endsAt, Integer feeCents) {
        this.workspace = workspace;
        this.schedule = schedule;
        this.patient = patient;
        this.therapist = therapist;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.feeCents = feeCents;
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

    public RecurringSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(RecurringSchedule schedule) {
        this.schedule = schedule;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getTherapist() {
        return therapist;
    }

    public void setTherapist(User therapist) {
        this.therapist = therapist;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getFeeCents() {
        return feeCents;
    }

    public void setFeeCents(Integer feeCents) {
        this.feeCents = feeCents;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
