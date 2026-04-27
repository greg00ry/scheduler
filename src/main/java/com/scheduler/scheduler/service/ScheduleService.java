package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.CreateShiftDTO;
import com.scheduler.scheduler.dto.ScheduleDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.model.WorkingHours;
import com.scheduler.scheduler.repository.ScheduleRepository;

import com.scheduler.scheduler.repository.UserRepository;
import com.scheduler.scheduler.repository.WorkingHoursRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShiftService shiftService;
    private final WorkingHoursRepository workingHoursRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, UserService userService, UserRepository userRepository, ShiftService shiftService, WorkingHoursRepository workingHoursRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.shiftService = shiftService;
        this.workingHoursRepository = workingHoursRepository;
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

        shiftDTOS.forEach(s -> s.setScheduleId(saved.getId()));

        shiftDTOS.stream()
                .map(shiftService::createShift)
                .toList();

        shiftDTOS.stream()
                .map(CreateShiftDTO::getUserId)
                .distinct()
                .forEach(u -> {
                    WorkingHours wh = new WorkingHours();
                    wh.setUser(userRepository.findById(u).orElseThrow());
                    wh.setSchedule(saved);
                    wh.setTotalHours(0);
                    wh.setOvertimeHours(0);
                    workingHoursRepository.save(wh);
                });

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
