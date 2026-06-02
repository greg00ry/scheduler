package com.scheduler.scheduler.controller;


import com.scheduler.scheduler.dto.organization.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.organization.OrganizationDTO;
import com.scheduler.scheduler.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/all}")
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/all/{ownerId}/{active}")
    public List<OrganizationDTO> getAllOwnerOrganizations(@PathVariable Long ownerId, @PathVariable boolean active) {
        return organizationService.getAllOwnerOrganizations(active, ownerId);
    }

    @PostMapping("/create")
    public OrganizationDTO createOrganization(@RequestBody CreateOrganizationDTO organization) {
        return organizationService.createOrganization(organization);
    }

    @PostMapping("/archive/{id}")
    public ResponseEntity<Void> archiveOrganization(@PathVariable Long id) {
        return organizationService.archiveOrganization(id);
    }
}
