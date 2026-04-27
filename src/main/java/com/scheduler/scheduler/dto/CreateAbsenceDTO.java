package com.scheduler.scheduler.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAbsenceDTO {
    private Long userId;
    private Long shiftId;
    private String reason;
    private LocalDateTime reportedAt;
}
