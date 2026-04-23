package com.scheduler.scheduler.controller;



import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.service.ScheduleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ScheduleDTO getSchedule(Long id) {
        return scheduleService.getSchedule(id);
    }

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }


}
