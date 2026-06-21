package com.scheduler.scheduler.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrganizationDTO {
    @NotBlank
    private String name;
    @NotNull
    private Long ownerId;
}
