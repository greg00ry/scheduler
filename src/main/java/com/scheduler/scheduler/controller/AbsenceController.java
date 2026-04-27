package com.scheduler.scheduler.controller;

import com.scheduler.scheduler.dto.CreateAbsenceDTO;
import com.scheduler.scheduler.service.AbsenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/absence")
public class AbsenceController {
    private final AbsenceService absenceService;
    public AbsenceController (AbsenceService absenceService) {
        this.absenceService = absenceService;
    }
    @GetMapping()
    public CreateAbsenceDTO createAbsence (CreateAbsenceDTO createAbsenceDTO) {
        return absenceService.createAbsence(createAbsenceDTO);
    }


}
