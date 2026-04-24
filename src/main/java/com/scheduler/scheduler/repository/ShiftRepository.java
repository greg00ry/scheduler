package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> getShiftsBySchedule_Id(Long id);
}
