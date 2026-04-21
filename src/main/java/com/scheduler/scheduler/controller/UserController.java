package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.dto.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    GetMapping
    public UserDTO getUser(Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/details")
    public UserDetailsDTO getUserDetails(Long id) {
        return userService.getUserDetails(id);
    }

    @GetMapping("/by-email")
    public Optional<User> findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/get-all-asc")
    public List<User> findAllByOrder() {
        return userService.findAllByOrderByLastNameAsc();
    }

    @GetMapping("/available")
    public List<User> getAvailableByDate(@RequestParam LocalDate date) {
        return userService.findAvailableUsersByDate(date);
    }

    @GetMapping("/by-role")
    public List<User> findByRole(@RequestParam Role role) {
        return userService.findByRole(role);
    }

    @PostMapping
    public User create(@RequestBody User employee) {
        return userService.save(employee);
    }
}
