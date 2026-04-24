package com.scheduler.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShiftDTO {
    @NotNull
    private Long id;
    @NotNull
    private UserDTO userDTO;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
}
