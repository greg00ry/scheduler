package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.model.AbsenceStatus;
import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.AvailabilityRepository;
import com.scheduler.scheduler.service.schedule.ScheduleValidator;
import com.scheduler.scheduler.service.schedule.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleValidatorTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    private ScheduleValidator validator;

    // Tydzień: Pon 22.06.2026 – Nd 28.06.2026
    private static final LocalDateTime MON = LocalDateTime.of(2026, 6, 22, 0, 0);
    private static final LocalDateTime TUE = LocalDateTime.of(2026, 6, 23, 0, 0);
    private static final LocalDateTime WED = LocalDateTime.of(2026, 6, 24, 0, 0);
    private static final LocalDateTime THU = LocalDateTime.of(2026, 6, 25, 0, 0);
    private static final LocalDateTime FRI = LocalDateTime.of(2026, 6, 26, 0, 0);
    private static final LocalDateTime SAT = LocalDateTime.of(2026, 6, 27, 0, 0);
    private static final LocalDateTime SUN = LocalDateTime.of(2026, 6, 28, 0, 0);
    private static final LocalDateTime WEEK_START = MON;
    private static final LocalDateTime WEEK_END   = LocalDateTime.of(2026, 6, 28, 23, 59, 59);

    // Polskie święto: 11 listopada 2026 (Święto Niepodległości)
    private static final LocalDateTime HOLIDAY      = LocalDateTime.of(2026, 11, 11, 0, 0);
    private static final LocalDateTime HOLIDAY_WEEK_START = LocalDateTime.of(2026, 11, 9,  0, 0);
    private static final LocalDateTime HOLIDAY_WEEK_END   = LocalDateTime.of(2026, 11, 15, 23, 59, 59);

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CreateScheduleDTO schedule(LocalDateTime start, LocalDateTime end) {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setWeekStart(start);
        dto.setWeekEnd(end);
        return dto;
    }

    private CreateShiftDTO shift(Long userId, LocalDateTime day, int startHour, int endHour) {
        CreateShiftDTO dto = new CreateShiftDTO();
        dto.setUserId(userId);
        dto.setDate(day.withHour(0).withMinute(0).withSecond(0));
        dto.setStartTime(day.withHour(startHour).withMinute(0).withSecond(0));
        dto.setEndTime(day.withHour(endHour).withMinute(0).withSecond(0));
        return dto;
    }

    private CreateShiftDTO shiftCrossMidnight(Long userId, LocalDateTime startDay, int startHour, LocalDateTime endDay, int endHour) {
        CreateShiftDTO dto = new CreateShiftDTO();
        dto.setUserId(userId);
        dto.setDate(startDay.withHour(0).withMinute(0).withSecond(0));
        dto.setStartTime(startDay.withHour(startHour).withMinute(0).withSecond(0));
        dto.setEndTime(endDay.withHour(endHour).withMinute(0).withSecond(0));
        return dto;
    }

    private CreateScheduleDTO defaultSchedule() {
        return schedule(WEEK_START, WEEK_END);
    }

    // -------------------------------------------------------------------------
    // validateScheduleDates
    // -------------------------------------------------------------------------

    @Test
    void validateScheduleDates_shouldReturnViolation_whenWeekStartAfterWeekEnd() {
        ValidationResult result = validator.validate(List.of(), schedule(WEEK_END, WEEK_START));
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateScheduleDates_shouldReturnViolation_whenWeekStartEqualsWeekEnd() {
        ValidationResult result = validator.validate(List.of(), schedule(MON, MON));
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateScheduleDates_shouldPass_whenWeekStartBeforeWeekEnd() {
        ValidationResult result = validator.validate(List.of(), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // validateShiftIntegrity
    // -------------------------------------------------------------------------

    @Test
    void validateShiftIntegrity_shouldReturnViolation_whenEndTimeBeforeStartTime() {
        CreateShiftDTO s = shift(1L, MON, 16, 8);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateShiftIntegrity_shouldReturnViolation_whenEndTimeEqualsStartTime() {
        CreateShiftDTO s = shift(1L, MON, 8, 8);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateShiftIntegrity_shouldReturnViolation_whenShiftBeforeWeekStart() {
        CreateShiftDTO s = shift(1L, MON.minusDays(1), 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateShiftIntegrity_shouldReturnViolation_whenShiftAfterWeekEnd() {
        CreateShiftDTO s = shift(1L, SUN.plusDays(1), 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateShiftIntegrity_shouldPass_whenShiftOnWeekStart() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateShiftIntegrity_shouldPass_whenShiftOnWeekEnd() {
        CreateShiftDTO s = shift(1L, SUN, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // validateNoOverlap
    // -------------------------------------------------------------------------

    @Test
    void validateNoOverlap_shouldReturnViolation_whenShiftsOverlap() {
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, MON, 14, 22);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateNoOverlap_shouldPass_whenSecondShiftStartsExactlyAtEndOfFirst() {
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, MON, 16, 22);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.toLowerCase().contains("nakład"));
    }

    @Test
    void validateNoOverlap_shouldReturnViolation_onlyForUserWithOverlap() {
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, MON, 14, 22);
        CreateShiftDTO s3 = shift(2L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s1, s2, s3), defaultSchedule());
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0)).contains("1");
    }

    // -------------------------------------------------------------------------
    // validateDailyHours (Art. 129 KP — max 8h na dobę)
    // -------------------------------------------------------------------------

    @Test
    void validateDailyHours_shouldReturnViolation_whenDailyHoursExceeded() {
        CreateShiftDTO s = shift(1L, MON, 6, 15); // 9h
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateDailyHours_shouldPass_whenDailyHoursExactly8() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateDailyHours_shouldPass_whenTwoShiftsOnDifferentDaysEach8h() {
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, TUE, 8, 16);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateDailyHours_shouldReturnViolation_onlyForUserWhoExceeds() {
        CreateShiftDTO s1 = shift(1L, MON, 6, 15); // 9h
        CreateShiftDTO s2 = shift(2L, MON, 8, 16); // 8h
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0)).contains("1");
    }

    // -------------------------------------------------------------------------
    // validateWeeklyHours (Art. 131 KP — max 48h na tydzień)
    // -------------------------------------------------------------------------

    @Test
    void validateWeeklyHours_shouldReturnViolation_whenWeeklyHoursExceeded() {
        // 7 x 8h = 56h > 48h
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16),
                shift(1L, SUN, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("48"));
    }

    @Test
    void validateWeeklyHours_shouldPass_whenWeeklyHoursExactly48() {
        // 6 x 8h = 48h
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("48"));
    }

    @Test
    void validateWeeklyHours_shouldReturnViolation_onlyForUserWhoExceeds() {
        List<CreateShiftDTO> shifts = List.of(
                // user 1: 7 x 8h = 56h
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16), shift(1L, SUN, 8, 16),
                // user 2: 5 x 8h = 40h
                shift(2L, MON, 8, 16), shift(2L, TUE, 8, 16), shift(2L, WED, 8, 16),
                shift(2L, THU, 8, 16), shift(2L, FRI, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("1") && v.contains("48"));
        assertThat(result.violations()).noneMatch(v -> v.contains("2") && v.contains("48"));
    }

    // -------------------------------------------------------------------------
    // validateDailyRest (Art. 132 KP — min 11h odpoczynku dobowego)
    // -------------------------------------------------------------------------

    @Test
    void validateDailyRest_shouldReturnViolation_whenRestBetweenShiftsOnSameDayTooShort() {
        // MON 06:00–12:00, MON 19:00–21:00 → przerwa 7h < 11h
        CreateShiftDTO s1 = shift(1L, MON, 6, 12);
        CreateShiftDTO s2 = shift(1L, MON, 19, 21);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("11") || v.contains("odpoczynku"));
    }

    @Test
    void validateDailyRest_shouldReturnViolation_whenRestBetweenShiftsAcrossDaysTooShort() {
        // MON 20:00–23:00, TUE 06:00–14:00 → przerwa 7h < 11h
        CreateShiftDTO s1 = shift(1L, MON, 20, 23);
        CreateShiftDTO s2 = shift(1L, TUE, 6, 14);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("11") || v.contains("odpoczynku"));
    }

    @Test
    void validateDailyRest_shouldPass_whenRestBetweenShiftsExactly11h() {
        // MON 08:00–16:00, TUE 03:00–11:00 → przerwa dokładnie 11h
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, TUE, 3, 11);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("odpoczynku dobowego") || v.contains("11h"));
    }

    @Test
    void validateDailyRest_shouldReturnViolation_onlyForUserWhoViolates() {
        // user 1: MON 20:00–23:00, TUE 06:00–14:00 → 7h (naruszenie)
        CreateShiftDTO s1 = shift(1L, MON, 20, 23);
        CreateShiftDTO s2 = shift(1L, TUE, 6, 14);
        // user 2: MON 08:00–16:00, TUE 08:00–16:00 → 16h (ok)
        CreateShiftDTO s3 = shift(2L, MON, 8, 16);
        CreateShiftDTO s4 = shift(2L, TUE, 8, 16);
        ValidationResult result = validator.validate(List.of(s1, s2, s3, s4), defaultSchedule());
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0)).contains("1");
    }

    // -------------------------------------------------------------------------
    // validateWeeklyRest (Art. 133 KP — min 35h odpoczynku tygodniowego)
    // -------------------------------------------------------------------------

    @Test
    void validateWeeklyRest_shouldReturnViolation_whenNoGapOf35hInWeek() {
        // 7 x 8h (08:00–16:00) — max przerwa między zmianami = 16h < 35h
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16),
                shift(1L, SUN, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("35"));
    }

    @Test
    void validateWeeklyRest_shouldPass_whenGapAfterLastShiftIsAtLeast35h() {
        // Pon–Pt 08:00–16:00, potem wolne Sob+Nd → przerwa Pt 16:00 do Nd 23:59 ≈ 56h > 35h
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("35"));
    }

    @Test
    void validateWeeklyRest_shouldPass_whenGapBeforeFirstShiftIsAtLeast35h() {
        // Pierwsza zmiana dopiero w Czw 08:00 → przerwa Pon 00:00 do Czw 08:00 = 80h > 35h
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16),
                shift(1L, SAT, 8, 16), shift(1L, SUN, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("35"));
    }

    // -------------------------------------------------------------------------
    // validateHolidayWork — ostrzeżenie, nie blok
    // -------------------------------------------------------------------------

    @Test
    void validateHolidayWork_shouldReturnWarning_whenShiftOnSunday() {
        CreateShiftDTO s = shift(1L, SUN, 8, 16); // 28.06.2026 = niedziela
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).isNotEmpty();
    }

    @Test
    void validateHolidayWork_shouldNotReturnViolation_whenShiftOnSunday() {
        CreateShiftDTO s = shift(1L, SUN, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateHolidayWork_shouldReturnWarning_whenShiftOnPolishHoliday() {
        CreateShiftDTO s = shift(1L, HOLIDAY, 8, 16); // 11.11.2026 Święto Niepodległości
        ValidationResult result = validator.validate(List.of(s), schedule(HOLIDAY_WEEK_START, HOLIDAY_WEEK_END));
        assertThat(result.warnings()).isNotEmpty();
    }

    @Test
    void validateHolidayWork_shouldNotReturnViolation_whenShiftOnPolishHoliday() {
        CreateShiftDTO s = shift(1L, HOLIDAY, 8, 16);
        ValidationResult result = validator.validate(List.of(s), schedule(HOLIDAY_WEEK_START, HOLIDAY_WEEK_END));
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateHolidayWork_shouldPass_whenShiftOnSaturday() {
        CreateShiftDTO s = shift(1L, SAT, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // validateNightWork (21:00–07:00) — ostrzeżenie, nie blok
    // -------------------------------------------------------------------------

    @Test
    void validateNightWork_shouldReturnWarning_whenShiftFullyAtNight() {
        // MON 22:00 – TUE 06:00
        CreateShiftDTO s = shiftCrossMidnight(1L, MON, 22, TUE, 6);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).anyMatch(v -> v.toLowerCase().contains("noc"));
    }

    @Test
    void validateNightWork_shouldReturnWarning_whenShiftPartiallyAtNight() {
        // MON 20:00–23:00 — część po 21:00
        CreateShiftDTO s = shift(1L, MON, 20, 23);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).anyMatch(v -> v.toLowerCase().contains("noc"));
    }

    @Test
    void validateNightWork_shouldNotReturnViolation_whenShiftAtNight() {
        CreateShiftDTO s = shiftCrossMidnight(1L, MON, 22, TUE, 6);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateNightWork_shouldPass_whenShiftFullyInDay() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // validateAvailability
    // -------------------------------------------------------------------------

    @Test
    void validateAvailability_shouldReturnViolation_whenUserIsUnavailable() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        Availability unavailable = new Availability();
        unavailable.setAvailable(false);
        when(availabilityRepository.findByUser_IdAndDate(1L, MON)).thenReturn(Optional.of(unavailable));

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.toLowerCase().contains("dostępn"));
    }

    @Test
    void validateAvailability_shouldPass_whenUserIsAvailable() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        Availability available = new Availability();
        available.setAvailable(true);
        when(availabilityRepository.findByUser_IdAndDate(1L, MON)).thenReturn(Optional.of(available));

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateAvailability_shouldPass_whenNoAvailabilityRecord() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        when(availabilityRepository.findByUser_IdAndDate(1L, MON)).thenReturn(Optional.empty());

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // validateLeaveRequest (Absence z type=VACATION, status=APPROVED)
    // -------------------------------------------------------------------------

    @Test
    void validateLeaveRequest_shouldReturnViolation_whenUserHasApprovedLeave() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of(new Absence()));

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.toLowerCase().contains("urlop") || v.toLowerCase().contains("nieobecn"));
    }

    @Test
    void validateLeaveRequest_shouldPass_whenUserHasPendingLeave() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of());

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateLeaveRequest_shouldPass_whenUserHasRejectedLeave() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of());

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void validateLeaveRequest_shouldPass_whenLeaveEndsBeforeShiftDate() {
        CreateShiftDTO s = shift(1L, TUE, 8, 16);
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of());

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isEmpty();
    }
}
