package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.CreateAvailabilityDTO;
import com.scheduler.scheduler.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;
    public AvailabilityController (AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }
    @PostMapping()
    public CreateAvailabilityDTO createAvailability (@RequestBody @Valid CreateAvailabilityDTO createAvailabilityDTO) {
        return availabilityService.createAvailability(createAvailabilityDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability (@PathVariable Long id) {
        return availabilityService.deleteAvailability(id);
    }


}
