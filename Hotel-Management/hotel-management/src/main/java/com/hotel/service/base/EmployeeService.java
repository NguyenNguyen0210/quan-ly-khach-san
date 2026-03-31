package com.hotel.service.base;

import com.hotel.entity.Employee;
import java.util.List;

public interface EmployeeService {

    void addEmployee(Employee employee);

    Employee getEmployeeById(Long id);

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByRole(String role);

    Employee findByUsername(String username);

    Employee authenticate(String username, String password);

    void ensureDefaultManagerExists();

    void updateEmployee(Employee employee);

    void deleteEmployee(Long employeeId);
}
