package com.scheduler.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CreateShiftDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long scheduleId;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    private String status;
    private boolean isValid;
    private String validationMessage;
}
