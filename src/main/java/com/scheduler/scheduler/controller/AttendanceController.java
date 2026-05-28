package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.AttendanceDTO;
import com.scheduler.scheduler.model.Attendance;
import com.scheduler.scheduler.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/{id}")
    public List<AttendanceDTO> getAttendance(@PathVariable Long id) {
        return attendanceService.getAttendanceByUser(id);
    }

    @PostMapping()
    public ResponseEntity<String> createAttendance(@RequestBody String rfid) {
        if (rfid.length() != 10) {
            return ResponseEntity.badRequest().body("Invalid RFID length");
        }
        return attendanceService.markAttendance(rfid);
    }
}
