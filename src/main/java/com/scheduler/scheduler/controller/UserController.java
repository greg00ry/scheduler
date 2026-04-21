package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.dto.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDTO getUser(Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public List<UserDTO> getAll() {
        return userService.getAllUser();
    }

    @GetMapping("/details")
    public UserDetailsDTO getUserDetails(Long id) {
        return userService.getUserDetails(id);
    }



    @GetMapping("/available")
    public List<UserDTO> getAvailableByDate(@RequestParam LocalDateTime date) {
        return userService.findAvailableUsersByDate(date);
    }

    @GetMapping("/by-role")
    public List<UserDTO> findByRole(@RequestParam Role role) {
        return userService.findByRole(role);
    }

    @PostMapping
    public User create(@RequestBody User employee) {
        return userService.save(employee);
    }
}
