package com.hotel.security;

import com.hotel.entity.Employee;

import java.util.Arrays;

public final class AuthSession {

    private static Employee currentEmployee;

    private AuthSession() {
    }

    public static void login(Employee employee) {
        currentEmployee = employee;
    }

    public static void logout() {
        currentEmployee = null;
    }

    public static Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public static boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public static boolean hasAnyRole(String... roles) {
        if (currentEmployee == null || currentEmployee.getRole() == null || roles == null) {
            return false;
        }
        String currentRole = currentEmployee.getRole().trim().toUpperCase();
        return Arrays.stream(roles)
                .filter(role -> role != null && !role.isBlank())
                .map(role -> role.trim().toUpperCase())
                .anyMatch(currentRole::equals);
    }

    public static void requireRole(String action, String... roles) {
        if (!hasAnyRole(roles)) {
            throw new SecurityException("You do not have permission to " + action + ".");
        }
    }
}
