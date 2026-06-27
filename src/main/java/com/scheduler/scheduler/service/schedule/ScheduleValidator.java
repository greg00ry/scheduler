package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.schedule.ValidationResult;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {

    private final AvailabilityRepository availabilityRepository;

    private final AbsenceRepository absenceRepository;

    public ValidationResult validate(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule) {
            List<String> violations = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            DailyWorkHours(shifts, violations);
            WeeklyWorkHours(shifts, violations, schedule);
            MinRestBetweenShift(shifts, violations);
            validateScheduleDates(schedule, violations);
            validateShiftIntegrity(shifts, schedule, violations);
            validateNoOverlap(shifts, violations);
            return new ValidationResult(violations, warnings);
        }

        private void DailyWorkHours(List<CreateShiftDTO> shifts, List<String> violations) {
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) -> {
                        userShifts.stream()
                                .collect(Collectors.groupingBy(CreateShiftDTO::getDate))
                                .forEach((date, dayShifts) -> {
                                    long totalMinutes = dayShifts.stream()
                                            .mapToLong(s -> Duration.between(s.getStartTime(), s.getEndTime()).toMinutes())
                                            .sum();
                                    if (totalMinutes > 480) {
                                        violations.add("User " + userId + " ma wiecej niż 8H w dniu");
                                    }

                                });
                    });
        }
        private void WeeklyWorkHours(List<CreateShiftDTO> shifts, List<String> violations, CreateScheduleDTO schedule) {
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) -> {

                        userShifts.stream()
                                .collect(Collectors.groupingBy(s -> s.getDate().get(WeekFields.ISO.weekOfYear())))
                                .forEach((week, weekShifts) -> {
                                    long totalMinutes = weekShifts.stream()
                                            .mapToLong(s -> Duration.between(s.getStartTime(), s.getEndTime()).toMinutes())
                                            .sum();

                                    if (totalMinutes > 2880) {
                                        violations.add("User" + userId + "ma więcej godzin niż 48h");
                                    }
                                });
                    });
        }
        private void MinRestBetweenShift(List<CreateShiftDTO> shifts, List<String> violations) {
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) -> {
                        userShifts.sort(Comparator.comparing(CreateShiftDTO::getStartTime));
                        for (int i = 0; i < userShifts.size() - 1; i++) {
                            long restMinutes = Duration.between(
                                    userShifts.get(i).getEndTime(),
                                    userShifts.get(i + 1).getStartTime()
                            ).toMinutes();
                            if (restMinutes < 660) {
                                violations.add("User " + userId + " ma mniej niz 11h odpoczynku między zmianami");
                            }
                        }
                    });
        }
        private void validateScheduleDates (CreateScheduleDTO schedule, List<String> violations) {
            if (!schedule.getWeekStart().isBefore(schedule.getWeekEnd())) {
                violations.add("Schedule nie może zaczynać sie wcześniej niz konczyć");
            }
        }
        private void validateShiftIntegrity(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule, List<String> violations) {
            shifts.forEach((shiftDTO) -> {
                        if (!shiftDTO.getStartTime().isBefore(shiftDTO.getEndTime())) {
                            violations.add("Zmiana" + shiftDTO.getDate() + shiftDTO.getUserId() + " nie może zaczynac się wcześniej niż konczyć");
                        }
                        if (shiftDTO.getDate().isBefore(schedule.getWeekStart())
                                || shiftDTO.getDate().isAfter(schedule.getWeekEnd())) {
                            violations.add("Zmiana " + shiftDTO.getDate() + shiftDTO.getUserId() + " jest poza zakresem " + schedule);
                        }
                    });
        }
        private void validateNoOverlap(List<CreateShiftDTO> shifts, List<String> violations) {
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) -> {

                        userShifts.sort(Comparator.comparing(CreateShiftDTO::getStartTime));

                        for (int i = 0; i < userShifts.size() - 1; i++) {
                            if (userShifts.get(i).getEndTime().isAfter(userShifts.get(i + 1).getStartTime())) {
                                violations.add("User " + userId + ": nakładające się zmiany");
                            }
                        }
                    });
        }
        private void validateDailyHours() {}
        private void validateWeeklyHours() {}
        private void validateDailyRest() {}
        private void validateWeeklyRest() {}
        private void validateHolidayWork() {}
        private void validateNightWork() {}
        private void validateAvailability() {}
        private void validateLeaveRequest() {}

}