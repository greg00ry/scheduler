package com.scheduler.scheduler.controller;


import com.scheduler.scheduler.dto.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.OrganizationDTO;
import com.scheduler.scheduler.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/{id}")
    public OrganizationDTO getOrganization(@PathVariable Long id) {
        return organizationService.getOrganization(id);
    }

    @GetMapping("/all/{ownerId}")
    public List<OrganizationDTO> getAllOrganizations(@PathVariable Long ownerId) {
        return organizationService.getAllOrganizations(ownerId);
    }

    @PostMapping("/create")
    public OrganizationDTO createOrganization(@RequestBody CreateOrganizationDTO organization) {
        return organizationService.createOrganization(organization);
    }
}
