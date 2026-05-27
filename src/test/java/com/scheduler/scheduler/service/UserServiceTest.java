package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateUserDTO;
import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.exception.ExistingUserException;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getUser_shouldReturnUserDTO_whenUserExists() {
        //given

        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //when
        UserDTO result = userService.getUser(1L);

        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jan");
    }

    @Test
    void getUser_shouldThrowWhenNotExist() {
        //given
        when(userRepository.findById(2L)).thenReturn(Optional.empty());



        //then
        assertThatThrownBy(() -> userService.getUser(2L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsersDTO_WhenUsersExists() {
        //given
        List<User> users = TestDataFactory.createManyUsers();

        when(userRepository.findAll()).thenReturn(users);

        //when
        List <UserDTO> result = userService.getAllUser();

        //then
        assertThat(result).hasSize(users.size());
    }

    @Test
    void createUser_shouldReturnUserDTO_whenEmailIsUnique() {
        // given
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@test.com");
        dto.setPassword("password123");
        dto.setRole(Role.EMPLOYEE);

        User saved = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);

        when(userRepository.existsByEmail("jan@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");


        // when
        UserDTO result = userService.createUser(dto);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jan");
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        // given
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("jan@test.com");

        when(userRepository.existsByEmail("jan@test.com")).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ExistingUserException.class);
    }

    @Test
    void assignRFIDToUser_shouldReturnUserDTO_whenUserExists() {
        //given
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //when
        UserDTO result = userService.assignRFIDToUser("0000241127", 1L);

        //then
        assertThat(result.getId()).isEqualTo(1L);

    }
    @Test
    void assignRFIDToUser_shouldReturnUserDTO_whenUserNotFound() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.assignRFIDToUser("0000241127", 99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void assignRFIDToUser_shouldSetRFID_whenUserExists() {
        //given
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        userService.assignRFIDToUser("0000241127", 1L);

        //then
        assertThat(user.getRFIDCard()).isEqualTo("0000241127");
    }
}
