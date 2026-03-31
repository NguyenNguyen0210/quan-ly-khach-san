package com.hotel.service.base;

import com.hotel.dao.EmployeeDAO;
import com.hotel.entity.Employee;
import com.hotel.security.EmployeeRoles;

import java.util.List;
import java.util.Objects;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee must not be null");
        Objects.requireNonNull(employee.getFullName(), "Employee name is required");
        Objects.requireNonNull(employee.getUsername(), "Employee username is required");
        Objects.requireNonNull(employee.getPassword(), "Employee password is required");
        Objects.requireNonNull(employee.getRole(), "Employee role is required");
        if (employeeDAO.findByUsername(employee.getUsername()) != null) {
            throw new IllegalStateException("Username already exists.");
        }
        employeeDAO.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        if (id == null) {
            return null;
        }
        return employeeDAO.findById(id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }

    @Override
    public List<Employee> getEmployeesByRole(String role) {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return employeeDAO.findByRole(role);
    }

    @Override
    public Employee findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return employeeDAO.findByUsername(username);
    }

    @Override
    public Employee authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        Employee employee = employeeDAO.findByUsername(username.trim());
        if (employee == null) {
            return null;
        }
        return password.equals(employee.getPassword()) ? employee : null;
    }

    @Override
    public void ensureDefaultManagerExists() {
        if (employeeDAO.findByUsername("admin") != null) {
            return;
        }
        Employee manager = new Employee();
        manager.setFullName("System Manager");
        manager.setUsername("admin");
        manager.setPassword("admin123");
        manager.setRole(EmployeeRoles.MANAGER);
        manager.setSalary(0.0);
        employeeDAO.save(manager);
    }

    @Override
    public void updateEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee must not be null");
        if (employee.getId() == null) {
            throw new IllegalArgumentException("Employee id is required for update.");
        }
        Employee existing = employeeDAO.findById(employee.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Employee not found.");
        }
        Employee byUsername = employeeDAO.findByUsername(employee.getUsername());
        if (byUsername != null && !byUsername.getId().equals(employee.getId())) {
            throw new IllegalStateException("Username already exists.");
        }
        employeeDAO.update(employee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee id is required for delete.");
        }
        Employee existing = employeeDAO.findById(employeeId);
        if (existing == null) {
            return;
        }
        employeeDAO.delete(existing);
    }
}
