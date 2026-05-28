package com.scheduler.scheduler.dto;

import java.time.LocalDateTime;

public class AttendanceDTO {
    private Long id;
    private UserDTO user;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private long duration;
}
