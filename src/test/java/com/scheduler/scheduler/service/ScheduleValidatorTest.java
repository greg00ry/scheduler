package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.CreateShiftDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleValidatorTest {

    private ScheduleValidator scheduleValidator;

    @BeforeEach
    void setUp() {
        scheduleValidator = new ScheduleValidator();
    }

    private CreateShiftDTO createShift(Long userId, int startHour, int endHour) {
        return createShiftOnDay(userId, 19, startHour, endHour);
    }

    private CreateShiftDTO createShiftOnDay(Long userId, int day, int startHour, int endHour) {
        CreateShiftDTO shift = new CreateShiftDTO();
        shift.setUserId(userId);
        shift.setDate(LocalDateTime.of(2026, 5, day, 0, 0));
        shift.setStartTime(LocalDateTime.of(2026, 5, day, startHour, 0));
        shift.setEndTime(LocalDateTime.of(2026, 5, day, endHour, 0));
        return shift;
    }

    private CreateShiftDTO createShiftAt(Long userId, int day, int startHour, int startMin, int endHour, int endMin) {
        CreateShiftDTO shift = new CreateShiftDTO();
        shift.setUserId(userId);
        shift.setDate(LocalDateTime.of(2026, 5, day, 0, 0));
        shift.setStartTime(LocalDateTime.of(2026, 5, day, startHour, startMin));
        shift.setEndTime(LocalDateTime.of(2026, 5, day, endHour, endMin));
        return shift;
    }

    @Test
    void validate_shouldReturnViolation_whenDailyHoursExceeded() {
        List<CreateShiftDTO> shifts = List.of(createShift(1L, 6, 17));
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validate_shouldReturnNoViolations_whenDailyHoursOk() {
        List<CreateShiftDTO> shifts = List.of(createShift(1L, 8, 16));
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_shouldReturnViolation_onlyForUserWhoExceeds() {
        List<CreateShiftDTO> shifts = List.of(
                createShift(1L, 6, 17),
                createShift(2L, 8, 16)
        );

        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0)).contains("1");
    }

    @Test
    void validate_shouldReturnNoViolations_whenListIsEmpty() {
        CreateScheduleDTO schedule = new CreateScheduleDTO();
        List<String> violations = scheduleValidator.validate(List.of(), schedule);

        assertThat(violations).isEmpty();
    }

    // WeeklyWorkhours tests — tydzień 21 (18-24 maja 2026)
    @Test
    void validate_shouldReturnViolation_whenWeeklyHoursExceeded() {
        // 7 dni x 8h = 56h > 48h
        List<CreateShiftDTO> shifts = List.of(
                createShiftOnDay(1L, 18, 8, 16),
                createShiftOnDay(1L, 19, 8, 16),
                createShiftOnDay(1L, 20, 8, 16),
                createShiftOnDay(1L, 21, 8, 16),
                createShiftOnDay(1L, 22, 8, 16),
                createShiftOnDay(1L, 23, 8, 16),
                createShiftOnDay(1L, 24, 8, 16)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).anyMatch(v -> v.contains("1") && v.contains("48"));
    }

    @Test
    void validate_shouldReturnNoViolations_whenWeeklyHoursOk() {
        // 6 dni x 8h = 48h — dokładnie limit, nie przekracza
        List<CreateShiftDTO> shifts = List.of(
                createShiftOnDay(1L, 18, 8, 16),
                createShiftOnDay(1L, 19, 8, 16),
                createShiftOnDay(1L, 20, 8, 16),
                createShiftOnDay(1L, 21, 8, 16),
                createShiftOnDay(1L, 22, 8, 16),
                createShiftOnDay(1L, 23, 8, 16)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_shouldReturnWeeklyViolation_onlyForUserWhoExceeds() {
        // user 1: 7 dni x 8h = 56h, user 2: 5 dni x 8h = 40h
        List<CreateShiftDTO> shifts = List.of(
                createShiftOnDay(1L, 18, 8, 16),
                createShiftOnDay(1L, 19, 8, 16),
                createShiftOnDay(1L, 20, 8, 16),
                createShiftOnDay(1L, 21, 8, 16),
                createShiftOnDay(1L, 22, 8, 16),
                createShiftOnDay(1L, 23, 8, 16),
                createShiftOnDay(1L, 24, 8, 16),
                createShiftOnDay(2L, 18, 8, 16),
                createShiftOnDay(2L, 19, 8, 16),
                createShiftOnDay(2L, 20, 8, 16),
                createShiftOnDay(2L, 21, 8, 16),
                createShiftOnDay(2L, 22, 8, 16)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0)).contains("1");
    }

    // MinRestBetweenShifts tests
    @Test
    void validate_shouldReturnViolation_whenRestBetweenShiftsTooShort() {
        // zmiana 1: 6:00-14:00, zmiana 2: 22:00-06:00 — tylko 8h odpoczynku
        List<CreateShiftDTO> shifts = List.of(
                createShiftAt(1L, 19, 6, 0, 14, 0),
                createShiftAt(1L, 19, 22, 0, 23, 59)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).anyMatch(v -> v.contains("1") && v.contains("11h"));
    }

    @Test
    void validate_shouldReturnNoViolations_whenRestBetweenShiftsOk() {
        // zmiana 1: 6:00-14:00, zmiana 2: dzień następny 8:00-16:00 — 18h odpoczynku
        List<CreateShiftDTO> shifts = List.of(
                createShiftOnDay(1L, 19, 6, 14),
                createShiftOnDay(1L, 20, 8, 16)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_shouldReturnRestViolation_onlyForUserWhoExceeds() {
        // user 1: zmiana 19-go 14:00-22:00, potem 20-go 6:00-14:00 — tylko 8h odpoczynku (naruszenie)
        // user 2: zmiana 19-go 6:00-14:00, potem 20-go 8:00-16:00 — 18h odpoczynku (ok)
        List<CreateShiftDTO> shifts = List.of(
                createShiftOnDay(1L, 19, 14, 22),
                createShiftOnDay(1L, 20, 6, 14),
                createShiftOnDay(2L, 19, 6, 14),
                createShiftOnDay(2L, 20, 8, 16)
        );
        CreateScheduleDTO schedule = new CreateScheduleDTO();

        List<String> violations = scheduleValidator.validate(shifts, schedule);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0)).contains("1");
    }


}
