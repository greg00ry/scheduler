package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.CreateUserDTO;
import com.scheduler.scheduler.dto.UpdateUserDTO;
import com.scheduler.scheduler.dto.UserDTO;
import com.scheduler.scheduler.dto.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public List<UserDTO> getAll() {
        return userService.getAllUser();
    }

    @GetMapping("/details")
    public UserDetailsDTO getUserDetails(@PathVariable Long id) {
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
    public UserDTO create(@RequestBody @Valid CreateUserDTO employee) {
        return userService.createUser(employee);
    }

    @PutMapping("/update")
    public UserDTO update(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}

//TODO: test added endpoints in postman
//TODO: schedule endpoint
//TODO: delete user endpoint
//TODO: delete schedule endpoint
//TODO: add validation for creating absence and availability that checks if user has existing them on that shift

