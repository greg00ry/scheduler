package com.scheduler.scheduler.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAvailabilityDTO {
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private boolean available;

}
