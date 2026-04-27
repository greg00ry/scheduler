package com.scheduler.scheduler.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAvailabilityDTO {
    @NotNull
    private Long userId;
    @NotNull
    private boolean available;
}
