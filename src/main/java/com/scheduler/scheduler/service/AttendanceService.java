package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.AttendanceDTO;
import com.scheduler.scheduler.model.Attendance;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.AttendanceRepository;
import com.scheduler.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public List<AttendanceDTO> getAttendanceByUser(Long userId) {
        return attendanceRepository.findAllByUser_Id(userId);
    }

    @Transactional
    public ResponseEntity<String> markAttendance(String rfid) {
        User user = userRepository.findByRFIDCard(rfid)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Optional<Attendance> existing = attendanceRepository.findByUserAndCheckOutIsNull(user);
        if (existing.isPresent()) {
            Attendance att = existing.get();
            att.setCheckOut(LocalDateTime.now());
            att.setDuration(Duration.between(att.getCheckIn(), att.getCheckOut()).toMinutes());
            attendanceRepository.save(att);
            return ResponseEntity.ok("CHECK_OUT");
        } else {
            Attendance att = new Attendance();
            att.setUser(user);
            att.setCheckIn(LocalDateTime.now());
            attendanceRepository.save(att);
            return ResponseEntity.ok("CHECK_IN");
        }
    }
}
