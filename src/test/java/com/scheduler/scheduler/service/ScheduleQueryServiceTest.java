package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.*;
import com.scheduler.scheduler.repository.ScheduleRepository;
import com.scheduler.scheduler.service.schedule.ScheduleQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    private Schedule schedule;
    private Organization organization;
    private User creator;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Org");

        creator = new User();
        creator.setId(10L);

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setWeekStart(LocalDateTime.of(2025, 6, 2, 0, 0));
        schedule.setWeekEnd(LocalDateTime.of(2025, 6, 8, 23, 59));
        schedule.setStatus(ScheduleStatus.DRAFT);
        schedule.setOrganization(organization);
        schedule.setCreatedBy_id(creator);
    }

    @Test
    void getSchedule_returnsCorrectDTO() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        ScheduleDTO result = scheduleQueryService.getSchedule(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ScheduleStatus.DRAFT);
        assertThat(result.getOrganizationId()).isEqualTo(1L);
        assertThat(result.getCreatedBy_id()).isEqualTo(10L);
    }

    @Test
    void getSchedule_throwsWhenNotFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleQueryService.getSchedule(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Schedule not found");
    }

    @Test
    void getScheduleForManagerAndEmployee_returnsCorrectDTO() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        ScheduleDTO result = scheduleQueryService.getScheduleForManagerAndEmployee(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrganizationId()).isEqualTo(1L);
    }

    @Test
    void getScheduleForManagerAndEmployee_throwsWhenNotFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleQueryService.getScheduleForManagerAndEmployee(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Schedule not found");
    }

    @Test
    void getAllSchedules_returnsAllMappedDTOs() {
        Schedule second = new Schedule();
        second.setId(2L);
        second.setWeekStart(LocalDateTime.of(2025, 6, 9, 0, 0));
        second.setWeekEnd(LocalDateTime.of(2025, 6, 15, 23, 59));
        second.setStatus(ScheduleStatus.ACTIVE);
        second.setOrganization(organization);
        second.setCreatedBy_id(creator);

        when(scheduleRepository.findAll()).thenReturn(List.of(schedule, second));

        List<ScheduleDTO> result = scheduleQueryService.getAllSchedules();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(ScheduleStatus.DRAFT);
        assertThat(result.get(1).getStatus()).isEqualTo(ScheduleStatus.ACTIVE);
    }

    @Test
    void getAllSchedules_returnsEmptyListWhenNoSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of());

        List<ScheduleDTO> result = scheduleQueryService.getAllSchedules();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllSchedulesForManagerAndEmployee_returnsAllMappedDTOs() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<ScheduleDTO> result = scheduleQueryService.getAllSchedulesForManagerAndEmployee();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }
}
