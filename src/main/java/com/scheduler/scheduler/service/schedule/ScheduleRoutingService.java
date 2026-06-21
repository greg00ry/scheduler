package com.scheduler.scheduler.service.schedule;

import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleRoutingService {

    private final ScheduleQueryService scheduleQueryService;

    private String getRole(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return role;
    }

    public ScheduleDTO routeToGetSchedule(Long id, Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> scheduleQueryService.getSchedule(id);
            case "ROLE_MANAGER" -> scheduleQueryService.getScheduleForManagerAndEmployee(id);
            case "ROLE_EMPLOYEE" -> scheduleQueryService.getScheduleForManagerAndEmployee(id);
            case "ROLE_OWNER" -> scheduleQueryService.getScheduleForManagerAndEmployee(id);
            default -> throw new RuntimeException("Unauthorized");
        };
    }

    public List<ScheduleDTO> routeToGetAllSchedules(Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> scheduleQueryService.getAllSchedules();
            case "ROLE_MANAGER" -> scheduleQueryService.getAllSchedulesForManagerAndEmployee();
            case "ROLE_EMPLOYEE" -> scheduleQueryService.getAllSchedulesForManagerAndEmployee();
            case "ROLE_OWNER" -> scheduleQueryService.getAllSchedulesForManagerAndEmployee();
            default -> throw new RuntimeException("Unauthorized");
        };
    }


}