package com.scheduler.scheduler.dto.rfid;

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
