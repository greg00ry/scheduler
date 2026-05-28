package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AttendanceDTO {
    private Long id;
    private Long userid;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private long duration;
}
