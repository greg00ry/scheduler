package com.scheduler.scheduler.service.user;

import com.scheduler.scheduler.dto.absence.AbsenceDTO;
import com.scheduler.scheduler.dto.shift.ShiftDTO;
import com.scheduler.scheduler.dto.user.CreateUserDTO;
import com.scheduler.scheduler.dto.user.UpdateUserDTO;
import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.dto.workinghours.WorkingHoursDTO;
import com.scheduler.scheduler.exception.ExistingUserException;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.scheduler.scheduler.service.user.UserQueryService.getUserDetailsDTO;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;





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
        return getUserDetailsDTO(id, userRepository);
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

            User saved = userRepository.save(user);

            return createUserDTO(saved);
        }

    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setEmail(updateUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        User saved = userRepository.save(user);
        return createUserDTO(saved);
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found")));
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public UserDTO assignRFIDToUser(String rfid, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRFIDCard(rfid);
        userRepository.save(user);

        return createUserDTO(user);
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
