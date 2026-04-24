package com.scheduler.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

public class createScheduleDTO {
    @NotNull
    private LocalDateTime weekStart;
    @NotNull
    private LocalDateTime weekEnd;
    @NotNull
    private Long userId;
    @NotNull
    private List<ShiftDTO> shifts;
}
