package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}


