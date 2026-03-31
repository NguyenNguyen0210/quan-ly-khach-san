package com.hotel.security;

import java.util.List;

public final class EmployeeRoles {

    public static final String RECEPTIONIST = "RECEPTIONIST";
    public static final String MANAGER = "MANAGER";
    public static final String SERVICE_STAFF = "SERVICE_STAFF";

    private EmployeeRoles() {
    }

    public static List<String> all() {
        return List.of(RECEPTIONIST, MANAGER, SERVICE_STAFF);
    }

    public static String toDisplayName(String role) {
        if (role == null || role.isBlank()) {
            return "Unknown";
        }
        return switch (role.trim().toUpperCase()) {
            case RECEPTIONIST -> "Receptionist";
            case MANAGER -> "Manager";
            case SERVICE_STAFF -> "Service Staff";
            default -> role;
        };
    }
}
