package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.Organization;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.ScheduleRepository;
import com.scheduler.scheduler.service.schedule.ScheduleCommandService;
import com.scheduler.scheduler.service.schedule.ScheduleQueryService;
import com.scheduler.scheduler.service.user.UserCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @InjectMocks
    private ScheduleCommandService scheduleService;
    @Mock
    private UserCommandService userService;
    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    @Test
    void getSchedule_returnScheduleDTO_whenScheduleExists() {
        //given
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        User user = new User();
        user.setId(1L);
        schedule.setCreatedBy_id(user);
        Organization org = new Organization();
        org.setId(1L);
        schedule.setOrganization(org);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        //when
        ScheduleDTO result = scheduleQueryService.getSchedule(1L);

        //then
        assertThat(result.getId()).isEqualTo(1L);
    }
    @Test
    void getSchedule_shouldThrowWhenNotExist() {
        //given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> scheduleQueryService.getSchedule(1L)).isInstanceOf(RuntimeException.class);
    }
}
