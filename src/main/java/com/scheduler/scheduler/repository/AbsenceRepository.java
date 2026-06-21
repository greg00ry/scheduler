package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    boolean existsByShiftAndUser(Shift shift, User user);

    @Query
    List<Absence> findApprovedLeaveForUserOnDate(Long id, LocalDateTime date);
}


