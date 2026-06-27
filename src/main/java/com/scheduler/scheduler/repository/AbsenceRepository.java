package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    boolean existsByShiftAndUser(Shift shift, User user);

    @Query("SELECT a FROM Absence a WHERE a.user.id = :userId " +
            "AND a.status = com.scheduler.scheduler.model.AbsenceStatus.APPROVED " +
            "AND a.startDate <= :date AND a.endDate >= :date")
    List<Absence> findApprovedLeaveForUserOnDate(@Param("userId") Long userId, @Param("date") LocalDateTime date);
}


