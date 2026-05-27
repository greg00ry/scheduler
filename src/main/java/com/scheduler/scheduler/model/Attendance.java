package com.scheduler.scheduler.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
}
