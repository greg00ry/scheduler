package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.*;
import com.scheduler.scheduler.exception.ExistingUserException;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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



    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) throws ExistingUserException {

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new ExistingUserException("User email exists");
        } else {
            User user = new User();
            user.setFirstName(createUserDTO.getFirstName());
            user.setLastName(createUserDTO.getLastName());
            user.setEmail(createUserDTO.getEmail());
            user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
            user.setRole(createUserDTO.getRole());

            userRepository.save(user);

            return createUserDTO(user);
        }

    }

    private UserDTO createUserDTO (User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }
}
