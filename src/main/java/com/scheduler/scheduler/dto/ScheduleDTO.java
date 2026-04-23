package com.scheduler.scheduler.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Long id;
    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;
    private UserDTO createdBy_id;
}
