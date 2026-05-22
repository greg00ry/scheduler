package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateScheduleDTO;
import com.scheduler.scheduler.dto.CreateShiftDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {
        public List<String> validate(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule) {
            List<String> violations = new ArrayList<>();
            DailyWorkHours(shifts, violations);
            WeeklyWorkhours(shifts, violations, schedule);
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
        private void WeeklyWorkhours(List<CreateShiftDTO> shifts, List<String> violations, CreateScheduleDTO schedule) {
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
}