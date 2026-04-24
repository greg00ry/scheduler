package com.scheduler.scheduler.dto;

import java.time.LocalDateTime;

public class CreateShiftDTO {
    private Long userId;
    private Long scheduleId;
    private LocalDateTime date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private boolean isValid;
    private String validationMessage;
}
