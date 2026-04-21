package com.scheduler.scheduler.dto;

import jakarta.validation.constraints.NotNull;

public class WorkingHoursDTO {
    @NotNull
    private Long id;
    @NotNull
    private float totalHours;
    @NotNull
    private float overtimeHours;
}
