package com.scheduler.scheduler.service;

import com.scheduler.scheduler.model.Schedule;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.model.WorkingHours;
import com.scheduler.scheduler.repository.WorkingHoursRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WorkingHoursService {
    private final WorkingHoursRepository workingHoursRepository;
    public WorkingHoursService (WorkingHoursRepository workingHoursRepository) {
        this.workingHoursRepository = workingHoursRepository;
    }



    public void calculateHours (User user, Shift shift) {
        WorkingHours workingHours = workingHoursRepository.getByUser_Id(user.getId());

        Duration period = Duration.between(shift.getStartTime(), shift.getEndTime());

        workingHours.setTotalHours(workingHours.getTotalHours() + period.toMinutes() / 60f);
        workingHoursRepository.save(workingHours);
    }
    public void calculateOvertime(Schedule schedule, User user) {
        WorkingHours workingHours = workingHoursRepository.getByUser_Id(user.getId());

        workingHours.setOvertimeHours(workingHours.getOvertimeHours() - schedule.getWorkingHoursTarget());

        workingHoursRepository.save(workingHours);

    }
}
