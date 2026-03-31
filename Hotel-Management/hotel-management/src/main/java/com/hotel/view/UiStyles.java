package com.hotel.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UiStyles {

    public static final Color BACKGROUND = new Color(243, 246, 250);
    public static final Color PANEL = new Color(255, 255, 255);
    public static final Color PANEL_ALT = new Color(248, 250, 252);
    public static final Color BORDER = new Color(216, 223, 232);
    public static final Color TEXT = new Color(35, 46, 58);
    public static final Color MUTED = new Color(104, 117, 133);
    public static final Color PRIMARY = new Color(33, 99, 235);
    public static final Color SUCCESS = new Color(34, 139, 94);
    public static final Color WARNING = new Color(217, 142, 33);
    public static final Color DANGER = new Color(198, 74, 59);
    public static final Color STATUS_INFO_BG = new Color(232, 240, 254);
    public static final Color STATUS_SUCCESS_BG = new Color(228, 245, 234);
    public static final Color STATUS_WARNING_BG = new Color(255, 246, 224);
    public static final Color STATUS_ERROR_BG = new Color(252, 232, 230);

    private UiStyles() {
    }

    public static void stylePage(JComponent component) {
        component.setBackground(BACKGROUND);
        if (component instanceof JPanel panel) {
            panel.setOpaque(true);
        }
    }

    public static JPanel createCardPanel(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(PANEL);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(top, left, bottom, right)
        ));
        return panel;
    }

    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createHint(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(MUTED);
        return label;
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(createInputBorder());
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 34));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(createInputBorder());
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 34));
    }

    public static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JLabel createStatusLabel() {
        JLabel label = new JLabel("Ready");
        label.setOpaque(true);
        label.setBackground(STATUS_INFO_BG);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return label;
    }

    public static void setStatus(JLabel label, String message, Color background, Color foreground) {
        label.setText(message);
        label.setBackground(background);
        label.setForeground(foreground);
    }

    public static void setFieldValidation(JComponent component, boolean valid) {
        component.setBorder(valid ? createInputBorder() : createInvalidInputBorder());
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(225, 236, 255));
        table.setSelectionForeground(TEXT);
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(PANEL_ALT);
        header.setForeground(TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    public static JScrollPane wrapTable(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        title
                ),
                new EmptyBorder(6, 6, 6, 6)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public static Border createInputBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
    }

    public static Border createInvalidInputBorder() {
        return new CompoundBorder(
                new LineBorder(DANGER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
    }
}
