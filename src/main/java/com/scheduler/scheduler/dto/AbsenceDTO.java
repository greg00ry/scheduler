package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Shift;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class AbsenceDTO {
    @NotBlank
    private Long id;
    private Shift shift;
    private String reason;
    private LocalDateTime reportedAt;
}
