package com.scheduler.scheduler.controller;


import com.scheduler.scheduler.dto.OrganizationDTO;
import com.scheduler.scheduler.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @RequestMapping("/{id}")
    public OrganizationDTO getOrganization(@RequestParam Long id) {
        return organizationService.getOrganization(id);
    }

    @RequestMapping("/all")
    public Iterable<OrganizationDTO> getAllOrganizations(@RequestParam Long ownerId) {
        return organizationService.getAllOrganizations(ownerId);
    }
}
