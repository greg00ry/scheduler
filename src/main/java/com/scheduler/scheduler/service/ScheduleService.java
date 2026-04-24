package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.CreateShiftDTO;
import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.dto.ShiftDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.repository.ScheduleRepository;

import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShiftService shiftService;

    public ScheduleService(ScheduleRepository scheduleRepository, UserService userService, UserRepository userRepository, ShiftService shiftService) {
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.shiftService = shiftService;
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
    @Transactional
    public ScheduleDTO createSchedule(CreateScheduleDTO createScheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setWeekStart(createScheduleDTO.getWeekStart());
        schedule.setWeekEnd(createScheduleDTO.getWeekEnd());
        schedule.setCreatedBy_id(userRepository.findById(createScheduleDTO.getCreatedBy_id())
                .orElseThrow(() -> new RuntimeException("User not exists")));
        Schedule saved = scheduleRepository.save(schedule);
        List<CreateShiftDTO> shiftDTOS = createScheduleDTO.getShifts();

        List< ShiftDTO> dtos = shiftDTOS.stream()
                .map(shiftService::createShift)
                .toList();

        return createScheduleDTO(saved);
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
