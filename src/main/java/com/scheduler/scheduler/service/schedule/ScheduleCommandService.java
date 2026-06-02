package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.model.ScheduleStatus;
import com.scheduler.scheduler.model.WorkingHours;
import com.scheduler.scheduler.repository.OrganizationRepository;
import com.scheduler.scheduler.repository.ScheduleRepository;

import com.scheduler.scheduler.repository.UserRepository;
import com.scheduler.scheduler.repository.WorkingHoursRepository;
import com.scheduler.scheduler.service.shift.ShiftService;
import de.focus_shift.jollyday.core.HolidayCalendar;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleCommandService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ShiftService shiftService;
    private final WorkingHoursRepository workingHoursRepository;
    private final ScheduleValidator scheduleValidator;
    private final OrganizationRepository organizationRepository;

    ////////////////////////////////

    //TODO: Update schedule

    ////////////////////////////////

    @Transactional
    public ScheduleDTO createSchedule(CreateScheduleDTO createScheduleDTO) {

        Schedule schedule = new Schedule();

        schedule.setWeekStart(createScheduleDTO.getWeekStart());

        schedule.setWeekEnd(createScheduleDTO.getWeekEnd());

        schedule.setCreatedBy_id(userRepository.findById(createScheduleDTO.getCreatedBy_id())
                .orElseThrow(() -> new RuntimeException("User not exists")));

        schedule.setStatus(ScheduleStatus.DRAFT);

        schedule.setOrganization(organizationRepository.findById(createScheduleDTO.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not exists")));

        HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.POLAND));

        long workingDays = createScheduleDTO.getWeekStart().toLocalDate()
                .datesUntil(createScheduleDTO.getWeekEnd().toLocalDate())
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY)
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(d -> !holidayManager.isHoliday(d))
                .count();

        float targetHours = (float) workingDays * 8;

        schedule.setWorkingHoursTarget(targetHours);

        Schedule saved = scheduleRepository.save(schedule);


        List<CreateShiftDTO> shiftDTOS = createScheduleDTO.getShifts();

        List<String> violations = validate(createScheduleDTO);
        if (!violations.isEmpty()) {
            throw new RuntimeException("Schedule validation failed: " + violations);
        }

        shiftDTOS.stream()
                .map(CreateShiftDTO::getUserId)
                .distinct()
                .forEach(u -> {
                    WorkingHours wh = new WorkingHours();
                    wh.setUser(userRepository.findById(u).orElseThrow());
                    wh.setSchedule(saved);
                    wh.setTotalHours(0);
                    wh.setOvertimeHours(0);
                    workingHoursRepository.save(wh);
                });

        shiftDTOS.forEach(s -> s.setScheduleId(saved.getId()));

        shiftDTOS.stream()
                .map(shiftService::createShift)
                .toList();

        return createScheduleDTO(saved);
    }

    public ResponseEntity<Void> deleteSchedule(Long id) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        sch.setStatus(ScheduleStatus.ARCHIVED);
        scheduleRepository.save(sch);
        return ResponseEntity.noContent().build();
    }


    public List<String> validate(CreateScheduleDTO createScheduleDTO) {
        return scheduleValidator.validate(createScheduleDTO.getShifts(), createScheduleDTO);
    }


    private ScheduleDTO createScheduleDTO (Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setWeekStart(schedule.getWeekStart());
        dto.setWeekEnd(schedule.getWeekEnd());
        dto.setCreatedBy_id(schedule.getCreatedBy_id().getId());
        return dto;
    }

}
