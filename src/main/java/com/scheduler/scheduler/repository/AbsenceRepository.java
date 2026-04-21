package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}
