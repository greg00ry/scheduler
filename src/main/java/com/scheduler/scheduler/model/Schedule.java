package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;
import java.util.List;

@FilterDef(name = "organizationFilter", parameters = @ParamDef(name = "orgId", type = Long.class))
@Filter(name = "organizationFilter", condition = "organization_id = :orgId")
@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;

    private ScheduleStatus status;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "schedule")
    private List<Shift> shiftList;

    @OneToMany(mappedBy = "schedule")
    private List<WorkingHours> workingHours;

    private float workingHoursTarget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy_id;

}
