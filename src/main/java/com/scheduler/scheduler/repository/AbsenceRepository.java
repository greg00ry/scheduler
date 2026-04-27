package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    boolean existsByShiftAndUser(Shift shift, User user);
}


