package com.scheduler.scheduler.controller;



import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.dto.shift.ShiftDTO;
import com.scheduler.scheduler.service.schedule.ScheduleCommandService;

import com.scheduler.scheduler.service.schedule.ScheduleQueryService;
import com.scheduler.scheduler.service.shift.ShiftService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleCommandService scheduleService;
    private final ShiftService shiftService;
    private final ScheduleQueryService scheduleQueryService;


    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(scheduleQueryService.getSchedule(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleQueryService.getAllSchedules());
    }

    @GetMapping("/shift/{id}")
    public ResponseEntity<ShiftDTO> getShift(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(shiftService.getShift(id));
    }

    @GetMapping("/shift/all")
    public ResponseEntity<List<ShiftDTO>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/{id}/shifts")
    public ResponseEntity<List<ShiftDTO>> getShiftsBySchedule(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(shiftService.getShiftsByScheduleId(id));
    }

    @PostMapping()
    public ResponseEntity<ScheduleDTO> create(@RequestBody @Valid CreateScheduleDTO schedule) {
        return ResponseEntity.status(201).body(scheduleService.createSchedule(schedule));
    }

    @PostMapping("/validate")
    public ResponseEntity<List<String>> validate(@RequestBody @Valid CreateScheduleDTO schedule) {
        List<String> violations = scheduleService.validate(schedule);
        if (violations.isEmpty()) {
            return ResponseEntity.ok(violations);
        }
        return ResponseEntity.badRequest().body(violations);
    }


}
