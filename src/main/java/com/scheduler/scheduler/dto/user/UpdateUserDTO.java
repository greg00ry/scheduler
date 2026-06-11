package com.scheduler.scheduler.dto.user;

import com.scheduler.scheduler.model.Role;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
