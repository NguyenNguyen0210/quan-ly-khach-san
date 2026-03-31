package com.hotel.view;

import javax.swing.*;
import java.awt.*;

public final class UiMessages {

    private UiMessages() {
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, normalizeMessage(message), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    public static String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return "An unexpected error occurred.";
        }
        String normalized = message.trim();
        if (normalized.contains("ConstraintViolationException")) {
            return "This action could not be completed because the data is being used elsewhere.";
        }
        if (normalized.contains("SQL") || normalized.contains("JDBC") || normalized.contains("Communications link failure")) {
            return "Database connection error. Please check MySQL configuration and try again.";
        }
        return normalized;
    }
}
