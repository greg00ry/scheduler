package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.rfid.AttendanceDTO;
import com.scheduler.scheduler.model.Attendance;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.AttendanceRepository;
import com.scheduler.scheduler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AttendanceService attendanceService;

    @Test
    void markAttendance_shouldReturnCheckIn_whenNoOpenAttendance() {
        // given
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        user.setRFIDCard("0000241127");
        when(userRepository.findByRFIDCard("0000241127")).thenReturn(Optional.of(user));
        when(attendanceRepository.findByUserAndCheckOutIsNull(user)).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(i -> i.getArgument(0));

        // when
        ResponseEntity<String> result = attendanceService.markAttendance("0000241127");

        // then
        assertThat(result.getBody()).isEqualTo("CHECK_IN");
    }

    @Test
    void markAttendance_shouldReturnCheckOut_whenOpenAttendanceExists() {
        // given
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        user.setRFIDCard("0000241127");
        Attendance open = new Attendance();
        open.setUser(user);
        open.setCheckIn(LocalDateTime.now().minusHours(8));

        when(userRepository.findByRFIDCard("0000241127")).thenReturn(Optional.of(user));
        when(attendanceRepository.findByUserAndCheckOutIsNull(user)).thenReturn(Optional.of(open));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(i -> i.getArgument(0));

        // when
        ResponseEntity<String> result = attendanceService.markAttendance("0000241127");

        // then
        assertThat(result.getBody()).isEqualTo("CHECK_OUT");
    }

    @Test
    void markAttendance_shouldThrow_whenRFIDNotFound() {
        // given
        when(userRepository.findByRFIDCard("9999999999")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> attendanceService.markAttendance("9999999999"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAttendanceByUser_shouldReturnList_whenAttendanceExists() {
        // given
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        Attendance att = new Attendance();
        att.setId(1L);
        att.setUser(user);
        att.setCheckIn(LocalDateTime.now().minusHours(8));
        att.setCheckOut(LocalDateTime.now());

        when(attendanceRepository.findAllByUser_Id(1L)).thenReturn(List.of(att));

        // when
        List<AttendanceDTO> result = attendanceService.getAttendanceByUser(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserid()).isEqualTo(1L);
    }
}
