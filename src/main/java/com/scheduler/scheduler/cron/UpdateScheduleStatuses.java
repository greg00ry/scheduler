package com.scheduler.scheduler.cron;

import com.scheduler.scheduler.model.ScheduleStatus;
import com.scheduler.scheduler.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UpdateScheduleStatuses {
    private final ScheduleRepository scheduleRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateScheduleStatuses() {
        LocalDateTime now = LocalDateTime.now();
        scheduleRepository.findAll().forEach(schedule -> {
            if (schedule.getStatus() == ScheduleStatus.DRAFT
                    && now.isAfter(schedule.getWeekStart())
                    && now.isBefore(schedule.getWeekEnd())) {
                schedule.setStatus(ScheduleStatus.ACTIVE);
            } else if (schedule.getStatus() == ScheduleStatus.ACTIVE && now.isAfter(schedule.getWeekEnd())){
                schedule.setStatus(ScheduleStatus.ARCHIVED);
            }
        });
    }
}
