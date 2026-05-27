package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping()
    public ResponseEntity<String> createAttendance(@RequestBody String rfid) {
        if (rfid.length() != 10) {
            return ResponseEntity.badRequest().body("Invalid RFID length");
        }
        return attendanceService.markAttendance(rfid);
    }
}
