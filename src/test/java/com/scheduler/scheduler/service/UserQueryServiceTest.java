package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import com.scheduler.scheduler.service.user.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void getUser_shouldReturnUserDTO_whenUserExists() {
        User user = TestDataFactory.createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = UserQueryService.getUserDTO(1L, userRepository);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jan");
    }

    @Test
    void getUser_shouldThrowWhenNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> UserQueryService.getUserDTO(2L, userRepository))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsersDTO_whenUsersExist() {
        List<User> users = TestDataFactory.createManyUsers();
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userQueryService.getAllUsers();

        assertThat(result).hasSize(users.size());
    }
}
