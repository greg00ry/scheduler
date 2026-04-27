package com.scheduler.scheduler.dto;

import com.scheduler.scheduler.model.Role;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
