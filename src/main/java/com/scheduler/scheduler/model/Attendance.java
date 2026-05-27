package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private long duration;
}
