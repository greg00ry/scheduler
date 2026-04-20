package com.scheduler.scheduler.repository;

import com.scheduler.scheduler.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
