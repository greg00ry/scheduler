package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
