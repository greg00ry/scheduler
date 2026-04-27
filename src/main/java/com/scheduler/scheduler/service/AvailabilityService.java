package com.scheduler.scheduler.service;


import com.scheduler.scheduler.dto.CreateAvailabilityDTO;
import com.scheduler.scheduler.model.Availability;
import com.scheduler.scheduler.repository.AvailabilityRepository;
import com.scheduler.scheduler.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public CreateAvailabilityDTO createAvailability (CreateAvailabilityDTO createAvailabilityDTO) {
        Availability availability = new Availability();
        availability.setUser(userRepository.findById(createAvailabilityDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        availability.setDate(createAvailabilityDTO.getDate());
        availability.setAvailable(createAvailabilityDTO.isAvailable());

        Availability saved = availabilityRepository.save(availability);

        return createAvailabilityDTO;
    }

    @Transactional
    public ResponseEntity<Void> deleteAvailability(Long id) {
        availabilityRepository.delete(availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found")));
        return ResponseEntity.noContent().build();
    }

}
