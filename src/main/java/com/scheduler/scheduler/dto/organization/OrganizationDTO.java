package com.scheduler.scheduler.dto.organization;

import lombok.Data;

@Data
public class OrganizationDTO {
    private Long id;
    private String name;
    private Long ownerId;
}
