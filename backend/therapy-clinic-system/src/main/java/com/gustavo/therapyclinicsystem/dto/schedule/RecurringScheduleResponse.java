package com.gustavo.therapyclinicsystem.dto.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RecurringScheduleResponse(

        UUID recurringScheduleId,

        UUID workspaceId,
        String workspaceName,

        UUID patientId,
        String patientName,

        UUID therapistId,
        String therapistName,

        DayOfWeek dayOfWeek,
        LocalTime startTime,
        Integer durationMinutes,

        LocalDate startsOn,
        LocalDate endsOn,

        Integer intervalWeeks,
        Boolean active

) {
}