package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Shift;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AbsenceDTO {
    @NotNull
    private Long id;
    @NotNull
    private Shift shift;
    @NotBlank
    private String reason;
    @NotNull
    private LocalDateTime reportedAt;
}
