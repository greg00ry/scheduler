package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.schedule.ValidationResult;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import de.focus_shift.jollyday.core.HolidayCalendar;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {

    private final AvailabilityRepository availabilityRepository;

    private final AbsenceRepository absenceRepository;

    public ValidationResult validate(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule) {
            List<String> violations = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            validateScheduleDates(schedule, violations);
            validateShiftIntegrity(shifts, schedule, violations);
            Set<Long> usersWithOverlap = validateNoOverlap(shifts, violations);
            List<CreateShiftDTO> nonOverlapping = shifts.stream()
                    .filter(s -> !usersWithOverlap.contains(s.getUserId()))
                    .collect(Collectors.toList());
            DailyWorkHours(nonOverlapping, violations);
            WeeklyWorkHours(nonOverlapping, violations, schedule);
            MinRestBetweenShift(nonOverlapping, violations);
            validateWeeklyRest(nonOverlapping, schedule, violations);
            validateAvailability(shifts, violations);
            validateLeaveRequest(shifts, violations);
            validateHolidayWork(shifts, warnings);
            validateNightWork(shifts, warnings);
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
                                        violations.add("User " + userId + " ma wiecej niż 8H w dniu " + date.toLocalDate());
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
                                        violations.add("User " + userId + "ma więcej godzin niż 48h");
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
        private Set<Long> validateNoOverlap(List<CreateShiftDTO> shifts, List<String> violations) {
            Set<Long> usersWithOverlap = new HashSet<>();
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) -> {
                        userShifts.sort(Comparator.comparing(CreateShiftDTO::getStartTime));
                        for (int i = 0; i < userShifts.size() - 1; i++) {
                            if (userShifts.get(i).getEndTime().isAfter(userShifts.get(i + 1).getStartTime())) {
                                violations.add("User " + userId + ": nakładające się zmiany");
                                usersWithOverlap.add(userId);
                            }
                        }
                    });
            return usersWithOverlap;
        }
    private void validateWeeklyRest(List<CreateShiftDTO> shifts, CreateScheduleDTO schedule, List<String> violations) {
        shifts.stream()
                .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                .forEach((userId, userShifts) -> {
                    userShifts.sort(Comparator.comparing(CreateShiftDTO::getStartTime));

                    long maxRest = Duration.between(schedule.getWeekStart(), userShifts.get(0).getStartTime()).toMinutes();

                    for (int i = 0; i < userShifts.size() - 1; i++) {
                        long gap = Duration.between(userShifts.get(i).getEndTime(), userShifts.get(i + 1).getStartTime()).toMinutes();
                        maxRest = Math.max(maxRest, gap);
                    }

                    maxRest = Math.max(maxRest, Duration.between(userShifts.get(userShifts.size() - 1).getEndTime(), schedule.getWeekEnd()).toMinutes());

                    if (maxRest < 2100) {
                        violations.add("User " + userId + " nie ma 35h odpoczynku tygodniowego (Art. 133 KP)");
                    }
                });
    }
        private void validateHolidayWork(List<CreateShiftDTO> shifts, List<String> warnings) {
            HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.POLAND));
            shifts.stream()
                    .collect(Collectors.groupingBy(CreateShiftDTO::getUserId))
                    .forEach((userId, userShifts) ->
                            userShifts.stream()
                                    .map(s -> s.getDate().toLocalDate())
                                    .distinct()
                                    .forEach(date -> {
                                        if (date.getDayOfWeek() == DayOfWeek.SUNDAY || holidayManager.isHoliday(date)) {
                                            warnings.add("User " + userId + " pracuje w niedzielę/święto " + date + " — należy się dzień wolny");
                                        }
                                    }));
        }

        private void validateNightWork(List<CreateShiftDTO> shifts, List<String> warnings) {
            for (CreateShiftDTO s : shifts) {
                int startHour = s.getStartTime().getHour();
                int endHour = s.getEndTime().getHour();
                if (startHour >= 21 || startHour < 7 || endHour > 21) {
                    warnings.add("User " + s.getUserId() + " pracuje w porze nocnej (21:00–07:00)");
                }
            }
        }

        private void validateAvailability(List<CreateShiftDTO> shifts, List<String> violations) {
            for (CreateShiftDTO s : shifts) {
                availabilityRepository.findByUser_IdAndDate(s.getUserId(), s.getDate())
                        .ifPresent(availability -> {
                            if (!availability.isAvailable()) {
                                violations.add("User " + s.getUserId() + " jest niedostępny w dniu " + s.getDate().toLocalDate());
                            }
                        });
            }
        }

        private void validateLeaveRequest(List<CreateShiftDTO> shifts, List<String> violations) {
            for (CreateShiftDTO s : shifts) {
                List<?> leaves = absenceRepository.findApprovedLeaveForUserOnDate(s.getUserId(), s.getDate());
                if (!leaves.isEmpty()) {
                    violations.add("User " + s.getUserId() + " ma zatwierdzony urlop/nieobecność w dniu " + s.getDate().toLocalDate());
                }
            }
        }

}