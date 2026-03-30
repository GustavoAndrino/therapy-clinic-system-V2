package com.gustavo.therapyclinicsystem.dto.schedule;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RecurringScheduleResponse(
        UUID id,
        UUID workspaceId,
        UUID patientId,
        UUID therapistId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        Integer durationMinutes,
        Boolean active,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt
) {
}
