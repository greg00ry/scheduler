package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.absence.AbsenceDTO;
import com.scheduler.scheduler.dto.absence.CreateAbsenceDTO;
import com.scheduler.scheduler.service.absence.AbsenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/absence")
@RequiredArgsConstructor
public class AbsenceController {
    private final AbsenceService absenceService;

    @PostMapping()
    public ResponseEntity<AbsenceDTO> createAbsence (@RequestBody @Valid CreateAbsenceDTO createAbsenceDTO) {
        return ResponseEntity.status(201).body(absenceService.createAbsence(createAbsenceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence (@PathVariable @Positive Long id) {
        return absenceService.deleteAbsence(id);
    }
}
