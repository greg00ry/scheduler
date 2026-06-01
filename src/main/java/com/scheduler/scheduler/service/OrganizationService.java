package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.OrganizationDTO;
import com.scheduler.scheduler.model.Organization;
import com.scheduler.scheduler.repository.OrganizationRepository;
import com.scheduler.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationDTO getOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setOwnerId(organization.getOwner().getId());
        return dto;
    }
    public List<OrganizationDTO> getAllOrganizations(Long ownerId) {
        List<Organization> organizations = organizationRepository.findAllByOwnerId(ownerId);
        return organizations.stream()
                .map(organization -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setName(organization.getName());
                    dto.setOwnerId(organization.getOwner().getId());
                    return dto;
                }).toList();
    }
    public OrganizationDTO createOrganization(CreateOrganizationDTO createOrganizationDTO) {
        Organization organization = new Organization();
        organization.setName(createOrganizationDTO.getName());
        organization.setOwner(userRepository.findById(createOrganizationDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        organizationRepository.save(organization);
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName(organization.getName());
        dto.setOwnerId(organization.getOwner().getId());
        return dto;
    }
}
