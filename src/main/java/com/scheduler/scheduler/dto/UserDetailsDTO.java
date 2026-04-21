package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailsDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Role role;
    @NotNull
    private List<AbsenceDTO> absences;
    @NotNull
    private List<WorkingHoursDTO> workingHoursList;
}
