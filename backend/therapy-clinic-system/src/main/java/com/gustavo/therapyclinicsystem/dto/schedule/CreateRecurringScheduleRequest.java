package com.gustavo.therapyclinicsystem.dto.schedule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateRecurringScheduleRequest(

        @NotNull
        UUID patientId,

        @NotNull
        DayOfWeek dayOfWeek,

        @NotNull
        LocalTime startTime,

        @NotNull
        @Positive
        Integer durationMinutes,

        @NotNull
        LocalDate startsOn,

        LocalDate endsOn,

        @NotNull
        @Min(1)
        @Max(52)
        Integer intervalWeeks

) {
}