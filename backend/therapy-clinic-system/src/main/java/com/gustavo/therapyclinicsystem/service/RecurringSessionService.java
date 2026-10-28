package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.schedule.CreateRecurringScheduleRequest;
import com.gustavo.therapyclinicsystem.dto.schedule.RecurringScheduleResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.ForbiddenOperationException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.Patient;
import com.gustavo.therapyclinicsystem.model.RecurringSchedule;
import com.gustavo.therapyclinicsystem.model.SessionOccurrence;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.SessionStatus;
import com.gustavo.therapyclinicsystem.repository.PatientRepository;
import com.gustavo.therapyclinicsystem.repository.RecurringScheduleRepository;
import com.gustavo.therapyclinicsystem.repository.SessionOccurrenceRepository;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import com.gustavo.therapyclinicsystem.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringSessionService {

    private final RecurringScheduleRepository recurringScheduleRepository;
    private final SessionOccurrenceRepository sessionOccurrenceRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;
    private final CurrentUserService currentUserService;

    public RecurringSessionService(
            RecurringScheduleRepository recurringScheduleRepository,
            SessionOccurrenceRepository sessionOccurrenceRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            WorkspaceMembershipRepository workspaceMembershipRepository,
            CurrentUserService currentUserService
    ) {
        this.recurringScheduleRepository = recurringScheduleRepository;
        this.sessionOccurrenceRepository = sessionOccurrenceRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.workspaceMembershipRepository = workspaceMembershipRepository;
        this.currentUserService = currentUserService;
    }


    @Transactional
    public RecurringScheduleResponse createRecurringSession(
            CreateRecurringScheduleRequest request
    ) {

        User therapist = getCurrentUser();

        Patient patient =
                getPatient(request.patientId());

        validateTherapistCanSchedulePatient(
                therapist,
                patient
        );

        validateRecurringScheduleRequest(request);

        List<OccurrenceTime> occurrenceTimes =
                calculateOccurrenceTimes(request);

        validateNoRecurringConflicts(
                therapist.getId(),
                occurrenceTimes
        );

        RecurringSchedule schedule =
                createAndSaveRecurringSchedule(
                        request,
                        patient,
                        therapist
                );

        createOccurrences(
                schedule,
                occurrenceTimes
        );

        return toResponse(schedule);
    }


    // =========================================================
    // USER / PATIENT
    // =========================================================

    private User getCurrentUser() {

        UUID userId =
                currentUserService.getUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }


    private Patient getPatient(UUID patientId) {

        return patientRepository
                .findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found: " + patientId
                        )
                );
    }


    // =========================================================
    // AUTHORIZATION
    // =========================================================

    private void validateTherapistCanSchedulePatient(
            User therapist,
            Patient patient
    ) {

        WorkspaceMembership membership =
                workspaceMembershipRepository
                        .findByUserIdAndWorkspaceId(
                                therapist.getId(),
                                patient.getWorkspace().getId()
                        )
                        .orElseThrow(() ->
                                new ForbiddenOperationException(
                                        "User does not belong to the patient's workspace"
                                )
                        );

        if (!membership.getRole().canBeAssignedPatients()) {

            throw new ForbiddenOperationException(
                    "User cannot act as a therapist in this workspace"
            );
        }

        if (patient.getTherapist() == null ||
                !patient.getTherapist()
                        .getId()
                        .equals(therapist.getId())) {

            throw new BusinessRuleException(
                    "Patient is not assigned to the authenticated therapist"
            );
        }
    }


    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateRecurringScheduleRequest(
            CreateRecurringScheduleRequest request
    ) {

        if (request.durationMinutes() <= 0) {

            throw new BusinessRuleException(
                    "Session duration must be greater than zero"
            );
        }

        if (request.intervalWeeks() < 1) {

            throw new BusinessRuleException(
                    "Interval between sessions must be at least one week"
            );
        }

        if (request.endsOn() != null &&
                request.endsOn().isBefore(request.startsOn())) {

            throw new BusinessRuleException(
                    "Recurring schedule end date cannot be before start date"
            );
        }
    }


    // =========================================================
    // OCCURRENCE CALCULATION
    // =========================================================

    private List<OccurrenceTime> calculateOccurrenceTimes(
            CreateRecurringScheduleRequest request
    ) {

        List<OccurrenceTime> occurrences =
                new ArrayList<>();

        LocalDate occurrenceDate =
                request.startsOn()
                        .with(
                                TemporalAdjusters.nextOrSame(
                                        request.dayOfWeek()
                                )
                        );

        LocalDate generationEnd =
                getGenerationEndDate(request);

        // TODO: Replace system timezone with workspace timezone later.
        ZoneId zoneId =
                ZoneId.systemDefault();

        while (!occurrenceDate.isAfter(generationEnd)) {

            ZonedDateTime start =
                    ZonedDateTime.of(
                            occurrenceDate,
                            request.startTime(),
                            zoneId
                    );

            ZonedDateTime end =
                    start.plusMinutes(
                            request.durationMinutes()
                    );

            occurrences.add(
                    new OccurrenceTime(
                            start.toOffsetDateTime(),
                            end.toOffsetDateTime()
                    )
            );

            occurrenceDate =
                    occurrenceDate.plusWeeks(
                            request.intervalWeeks()
                    );
        }

        return occurrences;
    }


    private LocalDate getGenerationEndDate(
            CreateRecurringScheduleRequest request
    ) {

        LocalDate sixMonthsAhead =
                request.startsOn()
                        .plusMonths(6);

        if (request.endsOn() == null) {
            return sixMonthsAhead;
        }

        if (request.endsOn().isBefore(sixMonthsAhead)) {
            return request.endsOn();
        }

        return sixMonthsAhead;
    }


    // =========================================================
    // CONFLICT VALIDATION
    // =========================================================

    private void validateNoRecurringConflicts(
            UUID therapistId,
            List<OccurrenceTime> occurrenceTimes
    ) {

        if (occurrenceTimes.isEmpty()) {
            return;
        }

        OffsetDateTime rangeStart =
                occurrenceTimes
                        .getFirst()
                        .startAt();

        OffsetDateTime rangeEnd =
                occurrenceTimes
                        .getLast()
                        .endAt();

        List<SessionOccurrence> existingSessions =
                sessionOccurrenceRepository
                        .findByTherapistIdAndStartAtLessThanAndEndAtGreaterThan(
                                therapistId,
                                rangeEnd,
                                rangeStart
                        );

        for (OccurrenceTime candidate : occurrenceTimes) {

            for (SessionOccurrence existing : existingSessions) {

                boolean overlaps =
                        candidate.startAt()
                                .isBefore(existing.getEndsAt())
                                &&
                                candidate.endAt()
                                        .isAfter(existing.getStartsAt());

                if (overlaps) {

                    throw new BusinessRuleException(
                            "Recurring schedule conflicts with an existing session"
                    );
                }
            }
        }
    }


    // =========================================================
    // RECURRING SCHEDULE CREATION
    // =========================================================

    private RecurringSchedule createAndSaveRecurringSchedule(
            CreateRecurringScheduleRequest request,
            Patient patient,
            User therapist
    ) {

        RecurringSchedule schedule =
                new RecurringSchedule();

        schedule.setWorkspace(
                patient.getWorkspace()
        );

        schedule.setPatient(
                patient
        );

        schedule.setTherapist(
                therapist
        );

        schedule.setDayOfWeek(
                request.dayOfWeek()
        );

        schedule.setStartTime(
                request.startTime()
        );

        schedule.setDurationMinutes(
                request.durationMinutes()
        );

        schedule.setStartDate(
                request.startsOn()
        );

        schedule.setEndDate(
                request.endsOn()
        );

        schedule.setIntervalWeeks(
                request.intervalWeeks()
        );

        schedule.setActive(true);

        return recurringScheduleRepository
                .save(schedule);
    }


    // =========================================================
    // SESSION OCCURRENCE CREATION
    // =========================================================

    private void createOccurrences(
            RecurringSchedule schedule,
            List<OccurrenceTime> occurrenceTimes
    ) {

        List<SessionOccurrence> occurrences =
                occurrenceTimes
                        .stream()
                        .map(time ->
                                buildOccurrence(
                                        schedule,
                                        time
                                )
                        )
                        .toList();

        sessionOccurrenceRepository
                .saveAll(occurrences);
    }


    private SessionOccurrence buildOccurrence(
            RecurringSchedule schedule,
            OccurrenceTime time
    ) {

        SessionOccurrence occurrence =
                new SessionOccurrence();

        occurrence.setWorkspace(
                schedule.getWorkspace()
        );

        occurrence.setPatient(
                schedule.getPatient()
        );

        occurrence.setTherapist(
                schedule.getTherapist()
        );

        occurrence.setSchedule(
                schedule
        );

        occurrence.setStartsAt(
                time.startAt()
        );

        occurrence.setEndsAt(
                time.endAt()
        );

        occurrence.setStatus(
                SessionStatus.SCHEDULED
        );

        return occurrence;
    }


    // =========================================================
    // RESPONSE
    // =========================================================

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


    // =========================================================
    // INTERNAL DTO
    // =========================================================

    private record OccurrenceTime(
            OffsetDateTime startAt,
            OffsetDateTime endAt
    ) {
    }
}