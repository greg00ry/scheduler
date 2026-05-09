package com.scheduler.scheduler.service;

import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.model.WorkingHours;
import com.scheduler.scheduler.repository.WorkingHoursRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkingHoursTest {
    @Mock
    private WorkingHoursRepository workingHoursRepository;
    @InjectMocks
    private WorkingHoursService workingHoursService;

    @Test
    void calculateHours_shouldCalculateRight_whenValuesAreGood() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Grzegorz");
        user.setLastName("Trzaskoma");

        WorkingHours wh = new WorkingHours();
        wh.setUser(user);


    }
}
