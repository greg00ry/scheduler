package com.scheduler.scheduler.service;

import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;

public class TestDataFactory {
    public static User createUser(Long id, String firstName, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }
    public static void createManyUsers() {
        User user = createUser(1L, "Jan", "Kowalski", Role.EMPLOYEE);
        User user2 = createUser(2L, "Tomasz", "Jabłoński", Role.EMPLOYEE);
        User user3 = createUser(3L, "Grzegorz", "Trzaskoma", Role.ADMIN);
        User user4 = createUser(4L, "Jan", "Nowak", Role.MANAGER);
        User user5 = createUser(5L, "Tomasz", "Kowalski", Role.EMPLOYEE);
    }
}
