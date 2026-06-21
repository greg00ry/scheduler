package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.aop.SkipOrganizationFilter;
import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.repository.ScheduleRepository;
import com.scheduler.scheduler.security.annotation.AdminOnly;
import com.scheduler.scheduler.security.annotation.ManagerEmployeeOwnerOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;

    @AdminOnly
    @SkipOrganizationFilter
    public ScheduleDTO getSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return toDTO((schedule));
    }

    @ManagerEmployeeOwnerOnly
    public ScheduleDTO getScheduleForManagerAndEmployee(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return toDTO((schedule));
    }

    @AdminOnly
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toDTO).toList();
    }

    @ManagerEmployeeOwnerOnly
    public List<ScheduleDTO> getAllSchedulesForManagerAndEmployee() {
        return scheduleRepository.findAll().stream()
                .map(this::toDTO).toList();
    }

    private ScheduleDTO toDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setWeekStart(schedule.getWeekStart());
        dto.setWeekEnd(schedule.getWeekEnd());
        dto.setCreatedBy_id(schedule.getCreatedBy_id().getId());
        dto.setStatus(schedule.getStatus());
        dto.setOrganizationId(schedule.getOrganization().getId());
        return dto;
    }
}
