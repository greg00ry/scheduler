package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
