package com.scheduler.scheduler.service;

import com.scheduler.scheduler.dto.absence.AbsenceDTO;
import com.scheduler.scheduler.dto.absence.CreateAbsenceDTO;
import com.scheduler.scheduler.dto.shift.ShiftDTO;
import com.scheduler.scheduler.model.Absence;
import com.scheduler.scheduler.repository.AbsenceRepository;
import com.scheduler.scheduler.repository.ShiftRepository;
import com.scheduler.scheduler.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AbsenceService {
    private final AbsenceRepository absenceRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftService shiftService;


    @Transactional
    public AbsenceDTO createAbsence(CreateAbsenceDTO createAbsenceDTO) {

        if (absenceRepository.existsByShiftAndUser(shiftRepository.findById(createAbsenceDTO.getShiftId())
                .orElseThrow(),
                userRepository.findById(createAbsenceDTO.getUserId())
                        .orElseThrow())) {
            throw new RuntimeException("Absence already exists");
        }


        Absence absence = new Absence();
        absence.setUser(userRepository.findById(createAbsenceDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        absence.setShift(shiftRepository.findById(createAbsenceDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Shift not found")));
        absence.setReason(createAbsenceDTO.getReason());
        absence.setReportedAt(createAbsenceDTO.getReportedAt());

        Absence saved = absenceRepository.save(absence);

        AbsenceDTO dto = new AbsenceDTO();

        ShiftDTO shiftDTO = shiftService.createShiftDTO(saved.getShift());

        dto.setId(saved.getId());
        dto.setShift(shiftDTO);
        dto.setReason(saved.getReason());
        dto.setReportedAt(saved.getReportedAt());



        return dto;
    }

    @Transactional
    public ResponseEntity<Void> deleteAbsence(Long id) {
        absenceRepository.delete(absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found")));
        return ResponseEntity.noContent().build();
    }



}
