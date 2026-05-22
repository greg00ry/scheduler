package com.scheduler.scheduler.controller;



import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.dto.ShiftDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.service.ScheduleService;

import com.scheduler.scheduler.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ShiftService shiftService;



    @GetMapping("/{id}")
    public ScheduleDTO getSchedule(@PathVariable Long id) {
        return scheduleService.getSchedule(id);
    }

    @GetMapping("/all")
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/shift/{id}")
    public ShiftDTO getShift(@PathVariable Long id) {
        return shiftService.getShift(id);
    }

    @GetMapping("/shift/all")
    public List<ShiftDTO> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @GetMapping("/{id}/shifts")
    public List<ShiftDTO> getShiftsBySchedule(@PathVariable Long id) {
        return shiftService.getShiftsByScheduleId(id);
    }

    @PostMapping("/create")
    public ScheduleDTO create(@RequestBody CreateScheduleDTO schedule) {
        return scheduleService.createSchedule(schedule);
    }

    @PostMapping("/validate")
    public ResponseEntity<List<String>> validate(@RequestBody CreateScheduleDTO schedule) {
        List<String> violations = scheduleService.validate(schedule);
        if (violations.isEmpty()) {
            return ResponseEntity.ok(violations);
        }
        return ResponseEntity.badRequest().body(violations);
    }


}
