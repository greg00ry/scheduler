package com.scheduler.scheduler.service.workinghours;

import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.model.WorkingHours;
import com.scheduler.scheduler.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {
    private final WorkingHoursRepository workingHoursRepository;




    public void calculateHours(User user, Shift shift, Schedule schedule) {
        WorkingHours workingHours = workingHoursRepository.findByUser_IdAndSchedule_Id(user.getId(), schedule.getId())
                .orElseThrow(() -> new RuntimeException("WorkingHours not found for user " + user.getId() + " in schedule " + schedule.getId()));

        Duration period = Duration.between(shift.getStartTime(), shift.getEndTime());

        workingHours.setTotalHours(workingHours.getTotalHours() + period.toMinutes() / 60f);
        workingHoursRepository.save(workingHours);
    }

    public void calculateOvertime(Schedule schedule, User user) {
        WorkingHours workingHours = workingHoursRepository.findByUser_IdAndSchedule_Id(user.getId(), schedule.getId())
                .orElseThrow(() -> new RuntimeException("WorkingHours not found for user " + user.getId() + " in schedule " + schedule.getId()));

        workingHours.setOvertimeHours(workingHours.getTotalHours() - schedule.getWorkingHoursTarget());
        workingHoursRepository.save(workingHours);
    }
}
