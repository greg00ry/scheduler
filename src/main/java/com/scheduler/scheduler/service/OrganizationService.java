package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.organization.CreateOrganizationDTO;
import com.scheduler.scheduler.dto.organization.OrganizationDTO;
import com.scheduler.scheduler.model.Organization;
import com.scheduler.scheduler.repository.OrganizationRepository;
import com.scheduler.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<OrganizationDTO> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizations.stream()
                .map(organization -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId(organization.getId());
                    dto.setName(organization.getName());
                    dto.setOwnerId(organization.getOwner().getId());
                    return dto;
                }).toList();
    }
    public List<OrganizationDTO> getAllOwnerOrganizations(boolean isActive, Long ownerId) {
        List<Organization> organizations = organizationRepository.findAllByOwnerIdAndIsActive(ownerId, isActive);
        return organizations.stream()
                .map(organization -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId(organization.getId());
                    dto.setName(organization.getName());
                    dto.setOwnerId(organization.getOwner().getId());
                    return dto;
                }).toList();
    }
    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationDTO createOrganizationDTO) {
        Organization organization = new Organization();
        organization.setName(createOrganizationDTO.getName());
        organization.setOwner(userRepository.findById(createOrganizationDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        organization.setActive(true);
        organizationRepository.save(organization);
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName(organization.getName());
        dto.setOwnerId(organization.getOwner().getId());
        return dto;
    }
    @Transactional
    public ResponseEntity<Void> archiveOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.setActive(false);
        organizationRepository.save(org);
        return ResponseEntity.noContent().build();
    }
}
