package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.repository.ScheduleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    public ScheduleService(ScheduleRepository scheduleRepository, UserService userService) {
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
    }

    public ScheduleDTO getSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return createScheduleDTO((schedule));
    }

    //TODO: Check for unused classes
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::createScheduleDTO).toList();
    }






    private ScheduleDTO createScheduleDTO (Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setWeekStart(schedule.getWeekStart());
        dto.setWeekEnd(schedule.getWeekEnd());
        dto.setCreatedBy_id(userService.getUser(schedule.getCreatedBy_id().getId()));
        return dto;
    }
}
