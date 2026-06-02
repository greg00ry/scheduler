package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.availability.CreateAvailabilityDTO;
import com.scheduler.scheduler.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    @PostMapping()
    public ResponseEntity<CreateAvailabilityDTO> createAvailability (@RequestBody @Valid CreateAvailabilityDTO createAvailabilityDTO) {
        return ResponseEntity.status(201).body(availabilityService.createAvailability(createAvailabilityDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability (@PathVariable Long id) {
        return availabilityService.deleteAvailability(id);
    }


}
