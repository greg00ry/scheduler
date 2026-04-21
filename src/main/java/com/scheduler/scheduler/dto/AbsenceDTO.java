package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Shift;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbsenceDTO {
    @NotNull
    private Long id;
    @NotNull
    private ShiftDTO shift;
    @NotBlank
    private String reason;
    @NotNull
    private LocalDateTime reportedAt;
}
