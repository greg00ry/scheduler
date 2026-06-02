package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.rfid.AttendanceDTO;
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
        return attendanceRepository.findAllByUser_Id(userId).stream()
                .map(this::createAttendanceDTO).toList();
    }

    @Transactional
    public ResponseEntity<String> markAttendance(String rfid) {

        if (rfid.length() != 10) {
            return ResponseEntity.badRequest().body("Invalid RFID length");
        }

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
    private AttendanceDTO createAttendanceDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setUserid(attendance.getUser().getId());
        dto.setCheckIn(attendance.getCheckIn());
        dto.setCheckOut(attendance.getCheckOut());
        dto.setDuration(attendance.getDuration());
        return dto;
    }
}
