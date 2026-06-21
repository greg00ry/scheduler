package com.scheduler.scheduler.service.user;

import com.scheduler.scheduler.dto.user.UserDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoutingService {
    private final UserQueryService userQueryService;

    private String getRole(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return role;
    }


    public UserDTO routeToGetUser(Long id, Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> userQueryService.getUser(id);
            case "ROLE_MANAGER" -> userQueryService.getUserForManager(id);
            case "ROLE_EMPLOYEE" -> userQueryService.getUserForEmployee((Long) authentication.getDetails());
            case "ROLE_OWNER" -> userQueryService.getUserForManager(id);
            default -> throw new RuntimeException("Unauthorized");
        };
    }

    public UserDetailsDTO routeToGetUserDetails(Long id, Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> userQueryService.getUserDetails(id);
            case "ROLE_MANAGER" -> userQueryService.getUserDetailsForManager(id);
            case "ROLE_EMPLOYEE" -> userQueryService.getUserDetailsForEmployee((Long) authentication.getDetails());
            case "ROLE_OWNER" -> userQueryService.getUserDetailsForManager(id);
            default -> throw new RuntimeException("Unauthorized");
        };
    }

    public List<UserDTO> routeToGetAllUsers(Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> userQueryService.getAllUsers();
            case "ROLE_MANAGER" -> userQueryService.getAllUsersForManager();
            case "ROLE_OWNER" -> userQueryService.getAllUsersForManager();
            default -> throw new RuntimeException("Unauthorized");
        };
    }

    public List<UserDTO> routeToGetAllUsersByDate(LocalDateTime date, Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> userQueryService.findAvailableUsersByDate(date);
            case "ROLE_MANAGER" -> userQueryService.findAvailableUsersByDateForManager(date);
            case "ROLE_OWNER" -> userQueryService.findAvailableUsersByDateForManager(date);
            default -> throw new RuntimeException("Unauthorized");
        };
    }
    public List<UserDTO> routeToGetAllUsersByRole(Role role1, Authentication authentication) {
        String role = getRole(authentication);
        return switch (role) {
            case "ROLE_ADMIN" -> userQueryService.findByRole(role1);
            case "ROLE_MANAGER" -> userQueryService.findByRoleForManager(role1);
            case "ROLE_OWNER" -> userQueryService.findByRoleForManager(role1);
            default -> throw new RuntimeException("Unauthorized");
        };
    }
}
