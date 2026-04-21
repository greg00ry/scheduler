package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.WorkingHours;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UserDetailsDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Role role;
    @NotBlank
    private List<AbsenceDTO> absences;
    @NotBlank
    private List<WorkingHoursDTO> workingHoursList;
}
