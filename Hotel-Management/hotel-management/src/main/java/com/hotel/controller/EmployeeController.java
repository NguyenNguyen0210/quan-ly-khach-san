package com.hotel.controller;

import com.hotel.entity.Employee;
import com.hotel.service.base.EmployeeService;
import com.hotel.service.base.EmployeeServiceImpl;

import java.util.List;
import java.util.Objects;

public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeServiceImpl();

    public void addEmployee(Employee employee) {
        employeeService.addEmployee(employee);
    }

    public Employee getEmployeeById(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeService.getEmployeeById(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    public List<Employee> getEmployeesByRole(String role) {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return employeeService.getEmployeesByRole(role);
    }

    public Employee findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return employeeService.findByUsername(username);
    }

    public Employee authenticate(String username, String password) {
        return employeeService.authenticate(username, password);
    }

    public void ensureDefaultManagerExists() {
        employeeService.ensureDefaultManagerExists();
    }

    public void updateEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee must not be null");
        employeeService.updateEmployee(employee);
    }

    public void deleteEmployee(Long employeeId) {
        employeeService.deleteEmployee(employeeId);
    }
}
