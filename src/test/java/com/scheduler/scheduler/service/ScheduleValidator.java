package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateShiftDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ScheduleValidator {

    @Test
    void ReturnNoValidation_when_dataOK() {
        //given
        ScheduleValidator val = new ScheduleValidator();



    }
    private CreateShiftDTO createList(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        CreateShiftDTO shift = new CreateShiftDTO();
        shift.setUserId(userId);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        return shift;
    }
}
