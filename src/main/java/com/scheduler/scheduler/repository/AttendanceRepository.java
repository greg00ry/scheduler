package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Attendance;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndCheckOutIsNull(User user);

    List<Attendance> findAllByUser_Id(Long userId);
}
