package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    boolean existsByUserAndDate(User user, LocalDateTime date);
}
