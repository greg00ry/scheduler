package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    //Get users by their role
    List<User>  findByRole(Role role);


    @Query("SELECT u FROM User u JOIN u.availabilityList a WHERE a.date = :date AND a.available = true")
    List<User> findAvailableUsersByDate(@Param("date") LocalDateTime date);


}
