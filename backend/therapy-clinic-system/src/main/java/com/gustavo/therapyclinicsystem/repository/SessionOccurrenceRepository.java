package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.SessionOccurrence;
import com.gustavo.therapyclinicsystem.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionOccurrenceRepository extends JpaRepository<SessionOccurrence, UUID> {

    List<SessionOccurrence> findByPatientId(UUID patientId);

    List<SessionOccurrence> findByTherapistId(UUID therapistId);

    List<SessionOccurrence> findByWorkspaceId(UUID workspaceId);

    List<SessionOccurrence> findByTherapistIdAndStartsAtBetween(
            UUID therapistId,
            OffsetDateTime start,
            OffsetDateTime end
    );

    List<SessionOccurrence> findByPatientIdAndStartsAtBetween(
            UUID patientId,
            OffsetDateTime start,
            OffsetDateTime end
    );

    List<SessionOccurrence> findByPatientIdAndStatusAndPaidFalse(
            UUID patientId,
            SessionStatus status
    );
}
