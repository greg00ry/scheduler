package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {
        public List<String> validate(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule) {
            List<String> violations = new ArrayList<>();
            DailyWorkHours(shifts, violations);
            WeeklyWorkHours(shifts, violations, schedule);
            MinRestBetweenShift(shifts, violations);
            return violations;
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

}