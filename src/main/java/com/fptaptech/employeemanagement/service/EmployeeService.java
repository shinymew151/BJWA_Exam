package com.fptaptech.employeemanagement.service;

import com.fptaptech.employeemanagement.entity.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    void deleteEmployee(Long id);
    List<Employee> searchEmployees(String keyword);
}