package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.aop.SkipOrganizationFilter;
import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @SkipOrganizationFilter
    public ScheduleDTO getSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return toDTO((schedule));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE', 'OWNER')")
    public ScheduleDTO getScheduleForManagerAndEmployee(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return toDTO((schedule));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toDTO).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE', 'OWNER')")
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
