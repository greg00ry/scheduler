package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;

    @OneToMany(mappedBy = "schedule")
    private List<Shift> shiftList;

    @OneToMany(mappedBy = "schedule")
    private List<Shift> workingHours;

    private float workingHoursTarget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy_id;

}
