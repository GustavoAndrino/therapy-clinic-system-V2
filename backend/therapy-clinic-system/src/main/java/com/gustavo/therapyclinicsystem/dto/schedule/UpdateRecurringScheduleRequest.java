package com.gustavo.therapyclinicsystem.dto.schedule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateRecurringScheduleRequest(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull @Min(1) @Max(1440) Integer durationMinutes,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active
) {
}
