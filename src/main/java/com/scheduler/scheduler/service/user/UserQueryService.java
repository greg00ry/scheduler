package com.scheduler.scheduler.service.user;

import com.scheduler.scheduler.dto.absence.AbsenceDTO;
import com.scheduler.scheduler.dto.shift.ShiftDTO;
import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.dto.workinghours.WorkingHoursDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public UserDTO getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());

        return dto;
    }

    public List<UserDTO> getAllUser() {
        return userRepository.findAll().stream()
                .map(this::createUserDTO).toList();
    }

    public List<UserDTO> findAvailableUsersByDate(LocalDateTime date) {
        return userRepository.findAvailableUsersByDate(date).stream()
                .map(this::createUserDTO).toList();
    }

    public List<UserDTO> findByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::createUserDTO).toList();
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
    private UserDTO createUserDTO (User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setRfid(user.getRFIDCard());
        return dto;
    }

}
