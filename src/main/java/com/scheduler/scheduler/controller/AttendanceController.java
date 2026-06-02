package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.rfid.AttendanceDTO;
import com.scheduler.scheduler.service.attendance.AttendanceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/{id}")
    public ResponseEntity<List<AttendanceDTO>> getAttendance(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceByUser(id));
    }

    @PostMapping()
    public ResponseEntity<String> createAttendance(@RequestBody String rfid) {
        return attendanceService.markAttendance(rfid);
    }
}
