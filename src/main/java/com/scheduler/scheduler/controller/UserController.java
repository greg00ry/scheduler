package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/by-email")
    public Optional<User> findByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email);
    }

    @GetMapping("/get-all-asc")
    public List<User> findAllByOrder() {
        return userRepository.findAllByOrderByLastNameAsc();
    }

    @GetMapping("/available")
    public List<User> getAvailableByDate(@RequestParam LocalDate date) {
        return userRepository.findAvailableUsersByDate(date);
    }

    @GetMapping("/by-role")
    public List<User> findByRole(@RequestParam Role role) {
        return userRepository.findByRole(role);
    }

    @PostMapping
    public User create(@RequestBody User employee) {
        return userRepository.save(employee);
    }
}
