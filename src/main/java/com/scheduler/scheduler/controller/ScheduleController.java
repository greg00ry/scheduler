package com.scheduler.scheduler.controller;



import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.dto.ShiftDTO;
import com.scheduler.scheduler.service.ScheduleService;

import com.scheduler.scheduler.service.ShiftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ShiftService shiftService;

    public ScheduleController(ScheduleService scheduleService, ShiftService shiftService) {
        this.scheduleService = scheduleService;
        this.shiftService = shiftService;
    }

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


}
