package com.fptaptech.employeemanagement.controller;

import com.fptaptech.employeemanagement.entity.Employee;
import com.fptaptech.employeemanagement.exception.DuplicateEmailException;
import com.fptaptech.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    // Display list of employees
    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employee-list";
    }
    
    // Search employees
    @GetMapping("/search")
    public String searchEmployees(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("employees", employeeService.searchEmployees(keyword));
        model.addAttribute("keyword", keyword);
        return "employee-list";
    }
    
    // Show form for adding new employee
    @GetMapping("/new")
    public String showNewEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add New Employee");
        return "employee-form";
    }
    
    // Show form for editing existing employee
    @GetMapping("/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            model.addAttribute("employee", employee);
            model.addAttribute("pageTitle", "Edit Employee");
            return "employee-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found.");
            return "redirect:/employees";
        }
    }
    
    // Save employee (create or update)
    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        // Check for validation errors first
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", employee.getId() == null ? 
                             "Add New Employee" : "Edit Employee");
            return "employee-form";
        }
        
        try {
            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", 
                employee.getId() == null ? 
                "Employee added successfully!" : "Employee updated successfully!");
            return "redirect:/employees";
        } catch (DuplicateEmailException e) {
            // Handle duplicate email specifically
            model.addAttribute("pageTitle", employee.getId() == null ? 
                             "Add New Employee" : "Edit Employee");
            model.addAttribute("errorMessage", e.getMessage());
            return "employee-form";
        } catch (Exception e) {
            // Handle any other unexpected errors with a generic message
            model.addAttribute("pageTitle", employee.getId() == null ? 
                             "Add New Employee" : "Edit Employee");
            model.addAttribute("errorMessage", 
                "An error occurred while saving the employee. Please try again.");
            return "employee-form";
        }
    }
    
    // Delete employee
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Unable to delete employee. Please try again.");
        }
        return "redirect:/employees";
    }
}