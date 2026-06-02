package com.scheduler.scheduler.dto.schedule;

import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateScheduleDTO  {
    @NotNull
    private LocalDateTime weekStart;
    @NotNull
    private LocalDateTime weekEnd;
    @NotNull
    private Long createdBy_id;
    @NotNull
    private List<CreateShiftDTO> shifts;
}
