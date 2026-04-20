package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Find one user by email
    Optional<User> findByEmail(String email);

    List<User>  findByRole(Role)


}
