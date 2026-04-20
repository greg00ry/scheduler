package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy_id;

}
