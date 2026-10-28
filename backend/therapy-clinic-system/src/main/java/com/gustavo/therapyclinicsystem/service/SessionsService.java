package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.session.*;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.*;
import com.gustavo.therapyclinicsystem.repository.PatientRepository;
import com.gustavo.therapyclinicsystem.repository.SessionOccurrenceRepository;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionsService {

    private final SessionOccurrenceRepository sessionRepository;
    private final CurrentUserService currentUserService;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public SessionsService(
            SessionOccurrenceRepository sessionRepository,
            CurrentUserService currentUserService,
            PatientRepository patientRepository,
            UserRepository userRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionCalendarResponse> getMySessions(
            OffsetDateTime from,
            OffsetDateTime to
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

    @Transactional
    public SessionOccurrenceSummaryResponse createOneOffSession(
            CreateSessionOccurrenceRequest request
    ) {

        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new BusinessRuleException(
                    "Session end time must be after start time"
            );
        }

        Patient patient = patientRepository
                .findById(request.patientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found"
                        )
                );

        Workspace workspace =
                patient.getWorkspace();

        UUID therapistId =
                currentUserService.getUserId();

        User therapist = userRepository
                .findById(therapistId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Therapist not found"
                        )
                );

        boolean hasConflict =
                sessionRepository
                        .existsByTherapistIdAndStartAtLessThanAndEndAtGreaterThan(
                                therapist.getId(),
                                request.endsAt(),
                                request.startsAt()
                        );

        if (hasConflict) {
            throw new BusinessRuleException(
                    "The therapist already has a session during this time"
            );
        }

        SessionOccurrence oneOffSession =
                new SessionOccurrence(
                        workspace,
                        null,
                        patient,
                        therapist,
                        request.startsAt(),
                        request.endsAt(),
                        patient.getDefaultSessionFeeCents()
                );

        SessionOccurrence savedSession =
                sessionRepository.save(oneOffSession);

        return toSummaryResponse(savedSession);
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

    private SessionOccurrenceSummaryResponse toSummaryResponse(
            SessionOccurrence session
    ) {

        return new SessionOccurrenceSummaryResponse(
                session.getId(),
                session.getPatient().getId(),
                session.getTherapist().getId(),
                session.getWorkspace().getId(),
                session.getStartsAt(),
                session.getEndsAt(),
                session.getFeeCents(),
                session.getStatus()
        );
    }

    private RecurringScheduleResponse toResponse(
            RecurringSchedule schedule
    ) {

        return new RecurringScheduleResponse(
                schedule.getId(),

                schedule.getWorkspace().getId(),
                schedule.getWorkspace().getName(),

                schedule.getPatient().getId(),
                schedule.getPatient().getFullName(),

                schedule.getTherapist().getId(),
                schedule.getTherapist().getFullName(),

                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getDurationMinutes(),

                schedule.getStartDate(),
                schedule.getEndDate(),

                schedule.getIntervalWeeks(),
                schedule.getActive()
        );
    }
}