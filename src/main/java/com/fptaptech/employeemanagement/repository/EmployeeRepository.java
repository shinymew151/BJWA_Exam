package com.fptaptech.employeemanagement.repository;

import com.fptaptech.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Search by name (case-insensitive)
    List<Employee> findByNameContainingIgnoreCase(String name);
    
    // Search by department
    List<Employee> findByDepartmentContainingIgnoreCase(String department);
    
    // Search by position
    List<Employee> findByPositionContainingIgnoreCase(String position);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if email exists for a different employee (used when updating)
    boolean existsByEmailAndIdNot(String email, Long id);
    
    // Custom search query - search by name, department, or position
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.position) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);
}