package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.rfid.AssignRFIDDTO;
import com.scheduler.scheduler.dto.user.CreateUserDTO;
import com.scheduler.scheduler.dto.user.UpdateUserDTO;
import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.service.user.UserCommandService;
import com.scheduler.scheduler.service.user.UserRoutingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserRoutingService userRoutingService;



    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable @Positive Long id, Authentication authentication) {
        return ResponseEntity.ok(userRoutingService.routeToGetUser(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) LocalDateTime date,
            Authentication authentication) {
        if (role != null) return ResponseEntity.ok(userRoutingService.routeToGetAllUsersByRole(role, authentication));
        if (date != null) return ResponseEntity.ok(userRoutingService.routeToGetAllUsersByDate(date, authentication));
        return ResponseEntity.ok(userRoutingService.routeToGetAllUsers(authentication));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable @Positive Long id, Authentication authentication) {
        return ResponseEntity.ok(userRoutingService.routeToGetUserDetails(id, authentication));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid CreateUserDTO employee) {
        return ResponseEntity.status(201).body(userCommandService.createUser(employee));
    }

    @PatchMapping("/{id}/rfid")
    public ResponseEntity<UserDTO> createWithRFID(@PathVariable @Positive Long id,@RequestBody @Valid AssignRFIDDTO rfidDto) {
        return ResponseEntity.ok(userCommandService.assignRFIDToUser(rfidDto.getRfid(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable @Positive Long id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userCommandService.updateUser(id, updateUserDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable @Positive Long id) {
        return userCommandService.deleteUser(id);
    }
}
//TODO: update readme
//TODO: add archive schedule after a date
//TODO: accepts absences by manager and admin
//TODO: websockets for events with schedule
//TODO: easy company chat
//TODO: register endpoint
//TODO: blockchain to saved schedule to ensure if nobody will delete old schedules
//TODO: validation in UpdateUserDTO
//TODO: walidacja dla grafiku tworzonego wstecz