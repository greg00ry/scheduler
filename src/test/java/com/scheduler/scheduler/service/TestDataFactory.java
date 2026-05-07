package com.scheduler.scheduler.service;

import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;

import java.util.List;

public class TestDataFactory {
    public static User createUser(Long id, String firstName, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }
    public static List<User> createManyUsers() {
        return List.of(
                createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE),
                createUser(2L, "Tomasz", "Jabłoński", Role.EMPLOYEE),
                createUser(3L, "Grzegorz", "Trzaskoma", Role.ADMIN),
                createUser(4L, "Jan", "Nowak", Role.MANAGER),
                createUser(5L, "Tomasz", "Kowalski", Role.EMPLOYEE)
        );
    }
}
