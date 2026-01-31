package com.fptaptech.employeemanagement.service;

import com.fptaptech.employeemanagement.entity.Employee;
import com.fptaptech.employeemanagement.exception.DuplicateEmailException;
import com.fptaptech.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    @Override
    public Employee saveEmployee(Employee employee) {
        try {
            // Check if updating and email changed
            if (employee.getId() != null) {
                Employee existing = employeeRepository.findById(employee.getId()).orElse(null);
                if (existing != null && !existing.getEmail().equals(employee.getEmail())) {
                    // Email is being changed, check if new email already exists
                    if (employeeRepository.existsByEmailAndIdNot(employee.getEmail(), employee.getId())) {
                        throw new DuplicateEmailException(employee.getEmail());
                    }
                }
            } else {
                // Creating new employee, check if email exists
                if (employeeRepository.existsByEmail(employee.getEmail())) {
                    throw new DuplicateEmailException(employee.getEmail());
                }
            }
            
            return employeeRepository.save(employee);
        } catch (DuplicateEmailException e) {
            // Re-throw our custom exception
            throw e;
        } catch (DataIntegrityViolationException e) {
            // Handle any database constraint violations
            String message = e.getMessage();
            if (message != null && message.toLowerCase().contains("email")) {
                throw new DuplicateEmailException(employee.getEmail());
            }
            throw new RuntimeException("Unable to save employee due to a data conflict. Please check your input.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while saving the employee.");
        }
    }
    
    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
    
    @Override
    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return employeeRepository.findAll();
        }
        return employeeRepository.searchEmployees(keyword);
    }
}