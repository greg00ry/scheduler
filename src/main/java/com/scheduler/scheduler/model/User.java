package com.scheduler.scheduler.model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Data
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @OneToMany(mappedBy = "user")
    private List<WorkingHours> workingHoursList;

    @OneToMany(mappedBy = "user")
    private List<Absence> absenceList;

    @OneToMany(mappedBy = "user")
    private List<Availability> availabilityList;

    @Enumerated(EnumType.STRING)
    private Role role;


}
