package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    boolean existsByUserAndDate(User user, LocalDateTime date);
    Optional<Availability> findByUser_IdAndDate(Long id, LocalDateTime date);
}
