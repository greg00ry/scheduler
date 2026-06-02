package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.rfid.AssignRFIDDTO;
import com.scheduler.scheduler.dto.user.CreateUserDTO;
import com.scheduler.scheduler.dto.user.UpdateUserDTO;
import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) LocalDateTime date) {
        if (role != null) return ResponseEntity.ok(userService.findByRole(role));
        if (date != null) return ResponseEntity.ok(userService.findAvailableUsersByDate(date));
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.getUserDetails(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid CreateUserDTO employee) {
        return ResponseEntity.status(201).body(userService.createUser(employee));
    }

    @PutMapping("/rfid")
    public ResponseEntity<UserDTO> createWithRFID(@RequestBody @Valid AssignRFIDDTO employee) {
        return ResponseEntity.ok(userService.assignRFIDToUser(employee.getRfid(), employee.getId()));
    }

    @PutMapping()
    public ResponseEntity<UserDTO> update(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userService.updateUser(updateUserDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable @Positive Long id) {
        return userService.deleteUser(id);
    }
}
//TODO: update readme
//TODO: add archive schedule after a date
//TODO: accepts absences by manager and admin
//TODO: websockets for events with schedule
//TODO: easy company chat
//TODO: register endpoint
//TODO: blockchain to saved schedule to ensure if nobody will delete old schedules