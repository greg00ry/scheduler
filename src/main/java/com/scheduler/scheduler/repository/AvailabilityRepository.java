package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
