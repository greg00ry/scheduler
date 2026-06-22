package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.model.AbsenceStatus;
import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.AvailabilityRepository;
import com.scheduler.scheduler.service.schedule.ScheduleValidator;
import com.scheduler.scheduler.dto.schedule.ValidationResult;
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
public class ScheduleValidatorEdgeCaseTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    private ScheduleValidator validator;

    private static final LocalDateTime MON = LocalDateTime.of(2026, 6, 22, 0, 0);
    private static final LocalDateTime TUE = LocalDateTime.of(2026, 6, 23, 0, 0);
    private static final LocalDateTime WED = LocalDateTime.of(2026, 6, 24, 0, 0);
    private static final LocalDateTime THU = LocalDateTime.of(2026, 6, 25, 0, 0);
    private static final LocalDateTime FRI = LocalDateTime.of(2026, 6, 26, 0, 0);
    private static final LocalDateTime SAT = LocalDateTime.of(2026, 6, 27, 0, 0);
    private static final LocalDateTime SUN = LocalDateTime.of(2026, 6, 28, 0, 0);
    private static final LocalDateTime WEEK_START = MON;
    private static final LocalDateTime WEEK_END   = LocalDateTime.of(2026, 6, 28, 23, 59, 59);

    private CreateScheduleDTO defaultSchedule() {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setWeekStart(WEEK_START);
        dto.setWeekEnd(WEEK_END);
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

    private CreateShiftDTO shiftWithMinutes(Long userId, LocalDateTime day, int startHour, int startMin, int endHour, int endMin) {
        CreateShiftDTO dto = new CreateShiftDTO();
        dto.setUserId(userId);
        dto.setDate(day.withHour(0).withMinute(0).withSecond(0));
        dto.setStartTime(day.withHour(startHour).withMinute(startMin).withSecond(0));
        dto.setEndTime(day.withHour(endHour).withMinute(endMin).withSecond(0));
        return dto;
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — pusta lista zmian
    // -------------------------------------------------------------------------

    @Test
    void validate_shouldReturnNoViolations_whenShiftListIsEmpty() {
        ValidationResult result = validator.validate(List.of(), defaultSchedule());
        assertThat(result.violations()).isEmpty();
        assertThat(result.warnings()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — dobowe
    // -------------------------------------------------------------------------

    @Test
    void validateDailyHours_shouldReturnViolation_whenExceededByOneMinute() {
        // 8h 1min = 481 minut > 480 minut
        CreateShiftDTO s = shiftWithMinutes(1L, MON, 8, 0, 16, 1);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateDailyHours_shouldPass_whenExactly8hAcrossTwoShifts() {
        // dwie zmiany na ten sam dzień: 4h + 4h = 8h
        CreateShiftDTO s1 = shift(1L, MON, 8, 12);
        CreateShiftDTO s2 = shift(1L, MON, 14, 18);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("8h") || v.contains("dobowy"));
    }

    @Test
    void validateDailyHours_shouldReturnViolation_whenTwoShiftsOnSameDayExceed8h() {
        // 5h + 4h = 9h tego samego dnia
        CreateShiftDTO s1 = shift(1L, MON, 8, 13);
        CreateShiftDTO s2 = shift(1L, MON, 14, 18);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — tygodniowe
    // -------------------------------------------------------------------------

    @Test
    void validateWeeklyHours_shouldReturnViolation_whenExceededByOneMinute() {
        // 6 x 8h = 48h, plus 1 minuta extra
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16),
                shiftWithMinutes(1L, SUN, 8, 0, 8, 1)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("48"));
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — odpoczynek dobowy
    // -------------------------------------------------------------------------

    @Test
    void validateDailyRest_shouldReturnViolation_whenRestShorterThan11hByOneMinute() {
        // MON 08:00–16:00, MON 02:59–10:59 (TUE) → przerwa 10h59min < 11h
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shiftWithMinutes(1L, TUE, 2, 59, 10, 59);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).anyMatch(v -> v.contains("11") || v.contains("odpoczynku"));
    }

    @Test
    void validateDailyRest_shouldPass_whenSingleShiftInWeek() {
        // Tylko jedna zmiana — żadnych par do sprawdzenia
        CreateShiftDTO s = shift(1L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.contains("odpoczynku dobowego") || v.contains("11h"));
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — wielokrotne naruszenia tego samego użytkownika
    // -------------------------------------------------------------------------

    @Test
    void validate_shouldReturnMultipleViolations_whenUserViolatesMultipleRules() {
        // 7 x 8h = 56h (tygodniowe), brak 35h odpoczynku, praca w niedzielę
        List<CreateShiftDTO> shifts = List.of(
                shift(1L, MON, 8, 16), shift(1L, TUE, 8, 16), shift(1L, WED, 8, 16),
                shift(1L, THU, 8, 16), shift(1L, FRI, 8, 16), shift(1L, SAT, 8, 16),
                shift(1L, SUN, 8, 16)
        );
        ValidationResult result = validator.validate(shifts, defaultSchedule());
        assertThat(result.violations().size() + result.warnings().size()).isGreaterThan(1);
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — nakładanie zmian (overlap)
    // -------------------------------------------------------------------------

    @Test
    void validateNoOverlap_shouldReturnViolation_whenShiftsFullyContained() {
        // Zmiana 2 w całości zawiera się w zmianie 1
        CreateShiftDTO s1 = shift(1L, MON, 8, 18);
        CreateShiftDTO s2 = shift(1L, MON, 10, 14);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void validateNoOverlap_shouldPass_whenDifferentUsersHaveSameShift() {
        // Dwóch różnych userów ma identyczne zmiany — to ok
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(2L, MON, 8, 16);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).noneMatch(v -> v.toLowerCase().contains("nakład"));
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — dostępność + urlop jednocześnie
    // -------------------------------------------------------------------------

    @Test
    void validate_shouldReturnTwoViolations_whenUserUnavailableAndHasApprovedLeave() {
        CreateShiftDTO s = shift(1L, MON, 8, 16);

        Availability unavailable = new Availability();
        unavailable.setAvailable(false);
        when(availabilityRepository.findByUser_IdAndDate(1L, MON)).thenReturn(Optional.of(unavailable));
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of(new Absence()));

        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).hasSizeGreaterThanOrEqualTo(2);
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — praca nocna
    // -------------------------------------------------------------------------

    @Test
    void validateNightWork_shouldReturnWarning_whenShiftStartsExactlyAt21() {
        // Dokładnie o 21:00 — wchodzi w porę nocną
        CreateShiftDTO s = shift(1L, MON, 21, 23);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).anyMatch(v -> v.toLowerCase().contains("noc"));
    }

    @Test
    void validateNightWork_shouldPass_whenShiftEndsExactlyAt21() {
        // Zmiana kończy się o 21:00 — nie wchodzi w porę nocną
        CreateShiftDTO s = shift(1L, MON, 13, 21);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).noneMatch(v -> v.toLowerCase().contains("noc"));
    }

    @Test
    void validateNightWork_shouldReturnWarning_whenShiftStartsBeforeSevenAm() {
        // Zmiana 06:00–14:00 — zaczyna się przed 07:00
        CreateShiftDTO s = shift(1L, MON, 6, 14);
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.warnings()).anyMatch(v -> v.toLowerCase().contains("noc"));
    }

    // -------------------------------------------------------------------------
    // Przypadki brzegowe — niedziele i święta
    // -------------------------------------------------------------------------

    @Test
    void validateHolidayWork_shouldReturnOneWarning_whenMultipleUsersWorkOnSunday() {
        // Trzech różnych userów pracuje w niedzielę — każdy dostaje osobne ostrzeżenie
        CreateShiftDTO s1 = shift(1L, SUN, 8, 16);
        CreateShiftDTO s2 = shift(2L, SUN, 8, 16);
        CreateShiftDTO s3 = shift(3L, SUN, 8, 16);
        ValidationResult result = validator.validate(List.of(s1, s2, s3), defaultSchedule());
        assertThat(result.warnings()).hasSize(3);
    }

    @Test
    void validateHolidayWork_shouldNotDuplicateWarning_whenSameUserHasTwoShiftsOnSunday() {
        // Ten sam user ma dwie zmiany w niedzielę — jedno ostrzeżenie
        CreateShiftDTO s1 = shift(1L, SUN, 8, 12);
        CreateShiftDTO s2 = shift(1L, SUN, 14, 18);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.warnings()).hasSize(1);
    }

    // -------------------------------------------------------------------------
    // Integralność zmian — przypadki brzegowe
    // -------------------------------------------------------------------------

    @Test
    void validateShiftIntegrity_shouldReturnViolationForEachInvalidShift() {
        // Dwie nieprawidłowe zmiany (endTime przed startTime)
        CreateShiftDTO s1 = shift(1L, MON, 16, 8);
        CreateShiftDTO s2 = shift(2L, TUE, 18, 6);
        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void validateShiftIntegrity_shouldReturnViolation_whenShiftDateIsExactlyWeekStartMinusOneSecond() {
        CreateShiftDTO s = new CreateShiftDTO();
        s.setUserId(1L);
        s.setDate(WEEK_START.minusSeconds(1));
        s.setStartTime(WEEK_START.minusSeconds(1).withHour(8));
        s.setEndTime(WEEK_START.minusSeconds(1).withHour(16));
        ValidationResult result = validator.validate(List.of(s), defaultSchedule());
        assertThat(result.violations()).isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // Urlop — przypadki brzegowe
    // -------------------------------------------------------------------------

    @Test
    void validateLeaveRequest_shouldReturnViolation_forEachShiftOnApprovedLeaveDays() {
        // User ma dwie zmiany objęte urlopem
        CreateShiftDTO s1 = shift(1L, MON, 8, 16);
        CreateShiftDTO s2 = shift(1L, TUE, 8, 16);

        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), eq(MON)))
                .thenReturn(List.of(new Absence()));
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), eq(TUE)))
                .thenReturn(List.of(new Absence()));

        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations())
                .filteredOn(v -> v.toLowerCase().contains("urlop") || v.toLowerCase().contains("nieobecn"))
                .hasSize(2);
    }

    @Test
    void validateLeaveRequest_shouldOnlyViolateForUserWithApprovedLeave() {
        CreateShiftDTO s1 = shift(1L, MON, 8, 16); // user 1 ma urlop
        CreateShiftDTO s2 = shift(2L, MON, 8, 16); // user 2 nie ma urlopu

        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(1L), any()))
                .thenReturn(List.of(new Absence()));
        when(absenceRepository.findApprovedLeaveForUserOnDate(eq(2L), any()))
                .thenReturn(List.of());

        ValidationResult result = validator.validate(List.of(s1, s2), defaultSchedule());
        assertThat(result.violations())
                .filteredOn(v -> v.toLowerCase().contains("urlop") || v.toLowerCase().contains("nieobecn"))
                .hasSize(1);
        assertThat(result.violations().get(0)).contains("1");
    }
}
