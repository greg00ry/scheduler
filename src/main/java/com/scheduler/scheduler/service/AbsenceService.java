package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.CreateAbsenceDTO;
import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.ShiftRepository;
import com.scheduler.scheduler.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AbsenceService {
    private final AbsenceRepository absenceRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    public AbsenceService (AbsenceRepository absenceRepository, UserRepository userRepository, ShiftRepository shiftRepository) {
        this.absenceRepository = absenceRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @Transactional
    public CreateAbsenceDTO createAbsence(CreateAbsenceDTO createAbsenceDTO) {
        Absence absence = new Absence();
        absence.setUser(userRepository.findById(createAbsenceDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        absence.setShift(shiftRepository.findById(createAbsenceDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Shift not found")));
        absence.setReason(createAbsenceDTO.getReason());
        absence.setReportedAt(createAbsenceDTO.getReportedAt());

        Absence saved = absenceRepository.save(absence);

        return createAbsenceDTO;
    }

    @Transactional
    public ResponseEntity<Void> deleteAbsence(Long id) {
        absenceRepository.delete(absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found")));
        return ResponseEntity.noContent().build();
    }
}
