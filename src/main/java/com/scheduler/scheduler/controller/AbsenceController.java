package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.CreateAbsenceDTO;
import com.scheduler.scheduler.service.AbsenceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/absence")
public class AbsenceController {
    private final AbsenceService absenceService;
    public AbsenceController (AbsenceService absenceService) {
        this.absenceService = absenceService;
    }
    @GetMapping()
    public CreateAbsenceDTO createAbsence (@RequestBody @Valid CreateAbsenceDTO createAbsenceDTO) {
        return absenceService.createAbsence(createAbsenceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence (@PathVariable @Valid Long id) {
        return absenceService.deleteAbsence(id);
    }
}
