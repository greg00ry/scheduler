package com.scheduler.scheduler.controller;


import com.scheduler.scheduler.dto.organization.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.organization.OrganizationDTO;
import com.scheduler.scheduler.service.orgaznization.OrganizationService;
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

    @GetMapping()
    public List<OrganizationDTO> getAllOwnerOrganizations(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Boolean active) {
        if (ownerId != null && active != null) {
            return organizationService.getAllOwnerOrganizations(active, ownerId);
        }
        return organizationService.getAllOrganizations();
    }

    @PostMapping()
    public OrganizationDTO createOrganization(@RequestBody CreateOrganizationDTO organization) {
        return organizationService.createOrganization(organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveOrganization(@PathVariable Long id) {
        return organizationService.archiveOrganization(id);
    }
}
