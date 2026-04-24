package com.scheduler.scheduler.service;


import com.scheduler.scheduler.dto.ShiftDTO;
import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.model.Shift;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.ShiftRepository;
import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final UserService userService;

    public ShiftService(ShiftRepository shiftRepository, UserService userService) {
        this.shiftRepository = shiftRepository;
        this.userService = userService;
    }

    public ShiftDTO getShift(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        return createShiftDTO(shift);
    }

    public List<ShiftDTO> getAllShifts () {
        return shiftRepository.findAll().stream()
                .map(this::createShiftDTO).toList();
    }

    @Transactional


    private ShiftDTO createShiftDTO (Shift shift) {
        ShiftDTO dto = new ShiftDTO();
        dto.setId(shift.getId());
        dto.setUserDTO(userService.getUser(shift.getUser().getId()));
        dto.setDate(shift.getDate());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        return dto;
    }
}
