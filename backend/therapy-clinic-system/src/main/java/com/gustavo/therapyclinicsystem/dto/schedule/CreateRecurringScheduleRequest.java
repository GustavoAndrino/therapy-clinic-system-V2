package com.gustavo.therapyclinicsystem.dto.schedule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateRecurringScheduleRequest(
        @NotNull UUID workspaceId,
        @NotNull UUID patientId,
        @NotNull UUID therapistId,
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull @Min(1) @Max(1440) Integer durationMinutes,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active
) {
}
