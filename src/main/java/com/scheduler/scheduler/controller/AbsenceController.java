package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.CreateAbsenceDTO;
import com.scheduler.scheduler.service.AbsenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/absence")
@RequiredArgsConstructor
public class AbsenceController {
    private final AbsenceService absenceService;

    @PostMapping()
    public CreateAbsenceDTO createAbsence (@RequestBody @Valid CreateAbsenceDTO createAbsenceDTO) {
        return absenceService.createAbsence(createAbsenceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence (@PathVariable Long id) {
        return absenceService.deleteAbsence(id);
    }
}
