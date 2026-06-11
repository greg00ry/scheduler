package com.scheduler.scheduler.dto.shift;

import com.scheduler.scheduler.dto.user.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShiftDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
}
