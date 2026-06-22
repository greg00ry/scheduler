package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = true)
    private Shift shift;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AbsenceType type;
    private AbsenceStatus status;

    private String reason;
    private LocalDateTime reportedAt;

}
