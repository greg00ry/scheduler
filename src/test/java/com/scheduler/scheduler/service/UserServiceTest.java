package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

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


}
