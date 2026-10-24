package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.session.SessionCalendarResponse;
import com.gustavo.therapyclinicsystem.model.SessionOccurrence;
import com.gustavo.therapyclinicsystem.repository.SessionOccurrenceRepository;
import com.gustavo.therapyclinicsystem.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SessionOcurrenceService {

    private final SessionOccurrenceRepository sessionRepository;
    private final CurrentUserService currentUserService;

    public SessionOcurrenceService(
            SessionOccurrenceRepository sessionRepository,
            CurrentUserService currentUserService
    ) {
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<SessionCalendarResponse> getMySessions(
            Instant from,
            Instant to
    ) {

        UUID therapistId =
                currentUserService.getUserId();

        return sessionRepository
                .findByTherapistIdAndStartAtBetweenOrderByStartAtAsc(
                        therapistId,
                        from,
                        to
                )
                .stream()
                .map(this::toCalendarResponse)
                .toList();
    }

    private SessionCalendarResponse toCalendarResponse(
            SessionOccurrence session
    ) {

        return new SessionCalendarResponse(
                session.getId(),
                session.getPatient().getId(),
                session.getPatient().getFullName(),
                session.getWorkspace().getId(),
                session.getWorkspace().getName(),
                session.getStartsAt(),
                session.getEndsAt(),
                session.getStatus()
        );
    }
}
