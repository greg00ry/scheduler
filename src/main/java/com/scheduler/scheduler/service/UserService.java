package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.AbsenceDTO;
import com.scheduler.scheduler.dto.ShiftDTO;
import com.scheduler.scheduler.dto.UserDetailsDTO;
import com.scheduler.scheduler.dto.WorkingHoursDTO;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailsDTO getUserDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());

        dto.setAbsences(user.getAbsenceList().stream()
                .map(a -> {
                    AbsenceDTO absenceDTO = new AbsenceDTO();
                    absenceDTO.setId(a.getId());
                    absenceDTO.setReason(a.getReason());

                    ShiftDTO shiftDTO = new ShiftDTO();
                    shiftDTO.setId(a.getShift().getId());
                    shiftDTO.setDate(a.getShift().getDate());
                    shiftDTO.setStartTime(a.getShift().getStartTime());
                    shiftDTO.setEndTime(a.getShift().getEndTime());

                    absenceDTO.setShift(shiftDTO);

                    return absenceDTO;
                }).toList());

        dto.setWorkingHoursList(user.getWorkingHoursList().stream()
                .map(w -> {
                    WorkingHoursDTO workingHoursDTO = new WorkingHoursDTO();
                    workingHoursDTO.setId(w.getId());
                    workingHoursDTO.setTotalHours(w.getTotalHours());
                    workingHoursDTO.setOvertimeHours(w.getOvertimeHours());
                    return workingHoursDTO;
                }).toList());

        return dto;
    }
}
