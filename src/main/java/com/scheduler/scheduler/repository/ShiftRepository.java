package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
