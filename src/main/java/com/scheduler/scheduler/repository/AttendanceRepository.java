package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.dto.AttendanceDTO;
import com.scheduler.scheduler.model.Attendance;
import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndCheckOutIsNull(User user);

    List<AttendanceDTO> findAllByUser_Id(Long userId);
}
