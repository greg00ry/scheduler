package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.OrganizationDTO;
import com.scheduler.scheduler.model.Organization;
import com.scheduler.scheduler.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationDTO getOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName(organization.getName());
        dto.setOwnerId(organization.getOwner().getId());
        return dto;
    }
}
