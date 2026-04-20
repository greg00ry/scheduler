package com.scheduler.scheduler.repository;


import com.scheduler.scheduler.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
}
