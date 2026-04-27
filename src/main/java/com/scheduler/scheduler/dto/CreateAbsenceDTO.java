package com.scheduler.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAbsenceDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long shiftId;
    @NotBlank
    private String reason;
    @NotNull
    private LocalDateTime reportedAt;
}
