package com.scheduler.scheduler.repository;


import com.scheduler.scheduler.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    Optional<WorkingHours> findByUser_IdAndSchedule_Id(Long userId, Long scheduleId);
}
