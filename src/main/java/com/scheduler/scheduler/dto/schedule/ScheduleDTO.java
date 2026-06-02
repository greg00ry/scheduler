package com.scheduler.scheduler.dto.schedule;

import com.scheduler.scheduler.dto.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Long id;
    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;
    private Long createdBy_id;
    private boolean isActive;
    private Long organizationId;
}
