package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.JavaBean;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
