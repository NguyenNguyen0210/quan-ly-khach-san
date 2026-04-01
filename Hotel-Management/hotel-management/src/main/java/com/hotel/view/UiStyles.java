package com.hotel.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

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
    private static final DateTimeFormatter DATE_PICKER_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Icon SECTION_TITLE_ICON_DEFAULT = createInfoIcon();
    private static final Icon SECTION_TITLE_ICON_USER = createUserIcon();
    private static final Icon SECTION_TITLE_ICON_PASSWORD = createLockIcon();
    private static final Icon SECTION_TITLE_ICON_DATE = createCalendarIcon();
    private static final Icon SECTION_TITLE_ICON_PHONE = createPhoneIcon();
    private static final Icon SECTION_TITLE_ICON_EMAIL = createEmailIcon();
    private static final Icon SECTION_TITLE_ICON_ROOM = createRoomIcon();
    private static final Icon SECTION_TITLE_ICON_MONEY = createMoneyIcon();
    private static final Icon SECTION_TITLE_ICON_BOOKING = createBookingIcon();
    private static final Icon SECTION_TITLE_ICON_SERVICE = createServiceIcon();
    private static final Icon MENU_ICON_HOME = createHomeIcon();
    private static final Icon MENU_ICON_BOOK = createBookingIcon();
    private static final Icon MENU_ICON_ROOMS = createRoomIcon();
    private static final Icon MENU_ICON_SERVICE = createServiceIcon();
    private static final Icon MENU_ICON_CUSTOMER = createUserIcon();
    private static final Icon MENU_ICON_EMPLOYEE = createEmployeeIcon();
    private static final Icon MENU_ICON_INVOICES = createInvoiceIcon();
    private static final Icon MENU_ICON_LOGOUT = createLogoutIcon();

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
        label.setIcon(resolveSectionTitleIcon(text));
        label.setIconTextGap(8);
        return label;
    }

    public static JLabel createRequiredSectionTitle(String text) {
        JLabel label = createSectionTitle(text);
        label.setText("<html>" + text + " <span style='color:#C64A3B'>*</span></html>");
        return label;
    }

    public static JLabel createRequiredNoteLabel() {
        JLabel label = new JLabel("* indicates required fields");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        label.setForeground(DANGER);
        return label;
    }

    public static Icon resolveMenuIcon(String text) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "home" -> MENU_ICON_HOME;
            case "book" -> MENU_ICON_BOOK;
            case "rooms" -> MENU_ICON_ROOMS;
            case "service" -> MENU_ICON_SERVICE;
            case "customer" -> MENU_ICON_CUSTOMER;
            case "employee" -> MENU_ICON_EMPLOYEE;
            case "invoices" -> MENU_ICON_INVOICES;
            case "logout" -> MENU_ICON_LOGOUT;
            default -> SECTION_TITLE_ICON_DEFAULT;
        };
    }

    private static Icon resolveSectionTitleIcon(String text) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);

        if (containsAny(normalized, "username", "account", "customer", "employee", "guest", "name", "ten")) {
            return SECTION_TITLE_ICON_USER;
        }
        if (containsAny(normalized, "password", "pass", "mat khau", "pin")) {
            return SECTION_TITLE_ICON_PASSWORD;
        }
        if (containsAny(normalized, "date", "check-in", "check-out", "time", "ngay")) {
            return SECTION_TITLE_ICON_DATE;
        }
        if (containsAny(normalized, "phone", "mobile", "tel", "sdt")) {
            return SECTION_TITLE_ICON_PHONE;
        }
        if (containsAny(normalized, "email", "mail")) {
            return SECTION_TITLE_ICON_EMAIL;
        }
        if (containsAny(normalized, "room", "phong", "floor")) {
            return SECTION_TITLE_ICON_ROOM;
        }
        if (containsAny(normalized, "price", "total", "amount", "payment", "invoice", "gia", "tien")) {
            return SECTION_TITLE_ICON_MONEY;
        }
        if (containsAny(normalized, "booking", "book", "reservation")) {
            return SECTION_TITLE_ICON_BOOKING;
        }
        if (containsAny(normalized, "service", "amenity", "utility")) {
            return SECTION_TITLE_ICON_SERVICE;
        }
        return SECTION_TITLE_ICON_DEFAULT;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static Icon createInfoIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(PRIMARY);
        g2.fillOval(2, 2, 10, 10);
        g2.setColor(Color.WHITE);
        g2.fillRect(6, 5, 2, 4);
        g2.fillRect(6, 10, 2, 1);

        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createUserIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillOval(4, 1, 6, 6);
        g2.fillRoundRect(3, 7, 8, 6, 5, 5);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createLockIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.setStroke(new BasicStroke(1.7f));
        g2.drawRoundRect(4, 1, 6, 5, 4, 4);
        g2.fillRoundRect(3, 6, 8, 7, 2, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createCalendarIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(2, 2, 10, 10, 2, 2);
        g2.setColor(Color.WHITE);
        g2.fillRect(3, 4, 8, 2);
        g2.fillRect(4, 7, 2, 2);
        g2.fillRect(7, 7, 2, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createPhoneIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(4, 1, 6, 12, 2, 2);
        g2.setColor(Color.WHITE);
        g2.fillRect(5, 3, 4, 7);
        g2.fillRect(6, 11, 2, 1);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createEmailIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(1, 3, 12, 8, 2, 2);
        g2.setColor(Color.WHITE);
        g2.drawLine(2, 4, 7, 8);
        g2.drawLine(12, 4, 7, 8);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createRoomIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(1, 6, 12, 5, 2, 2);
        g2.fillRoundRect(2, 4, 3, 2, 1, 1);
        g2.fillRoundRect(9, 4, 3, 2, 1, 1);
        g2.fillRect(2, 11, 1, 2);
        g2.fillRect(11, 11, 1, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createMoneyIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillOval(2, 2, 10, 10);
        g2.setColor(Color.WHITE);
        g2.fillRect(6, 4, 2, 6);
        g2.fillRect(5, 5, 4, 1);
        g2.fillRect(5, 8, 4, 1);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createBookingIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(2, 2, 10, 10, 2, 2);
        g2.setColor(Color.WHITE);
        g2.fillRect(4, 5, 6, 1);
        g2.fillRect(4, 7, 4, 1);
        g2.fillOval(9, 7, 2, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createServiceIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillOval(2, 2, 10, 10);
        g2.setColor(Color.WHITE);
        g2.fillRect(6, 4, 2, 6);
        g2.fillRect(4, 6, 6, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createHomeIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillPolygon(new int[]{1, 7, 13}, new int[]{7, 1, 7}, 3);
        g2.fillRoundRect(3, 7, 8, 6, 2, 2);
        g2.setColor(Color.WHITE);
        g2.fillRect(6, 9, 2, 4);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createEmployeeIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillOval(2, 2, 4, 4);
        g2.fillOval(8, 2, 4, 4);
        g2.fillRoundRect(1, 7, 5, 5, 3, 3);
        g2.fillRoundRect(8, 7, 5, 5, 3, 3);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createInvoiceIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.fillRoundRect(2, 1, 10, 12, 2, 2);
        g2.setColor(Color.WHITE);
        g2.fillRect(4, 4, 6, 1);
        g2.fillRect(4, 6, 6, 1);
        g2.fillRect(4, 8, 4, 1);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createLogoutIcon() {
        int size = 14;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY);
        g2.drawRoundRect(1, 2, 7, 10, 2, 2);
        g2.fillRect(6, 6, 6, 2);
        g2.fillPolygon(new int[]{9, 12, 9}, new int[]{4, 7, 10}, 3);
        g2.dispose();
        return new ImageIcon(image);
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
        field.setMargin(new Insets(0, 8, 0, 8));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
    }

    public static void enableDatePicker(JTextField field) {
        field.setEditable(false);
        field.setCursor(new Cursor(Cursor.HAND_CURSOR));
        field.setToolTipText("Click to pick a date");
        DatePickerPopup popup = new DatePickerPopup(field);
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popup.show();
            }
        });
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(UIManager.getBorder("ComboBox.border"));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 34));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return label;
            }
        });
    }

    public static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFont(new Font("Dialog", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        installButtonDisabledStyle(button, background, Color.WHITE);
        return button;
    }

    public static void installButtonDisabledStyle(JButton button, Color enabledBackground, Color enabledForeground) {
        Border enabledBorder = button.getBorder();
        Color disabledBackground = Color.WHITE;
        Color disabledForeground = Color.BLACK;
        UIManager.put("Button.disabledText", disabledForeground);

        Runnable applyState = () -> {
            if (button.isEnabled()) {
                button.setBackground(enabledBackground);
                button.setForeground(enabledForeground);
                button.setBorder(enabledBorder);
            } else {
                button.setBackground(disabledBackground);
                button.setForeground(disabledForeground);
                button.setBorder(enabledBorder);
            }
        };

        button.addPropertyChangeListener("enabled", evt -> applyState.run());
        applyState.run();
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

    private static final class DatePickerPopup {
        private final JTextField target;
        private final JPopupMenu popup;
        private final JLabel monthLabel;
        private final JPanel daysPanel;
        private YearMonth displayedMonth;
        private LocalDate selectedDate;

        private DatePickerPopup(JTextField target) {
            this.target = target;
            this.popup = new JPopupMenu();
            this.monthLabel = new JLabel("", SwingConstants.CENTER);
            this.daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));

            JPanel root = new JPanel(new BorderLayout(8, 8));
            root.setBorder(new EmptyBorder(8, 8, 8, 8));
            root.setBackground(Color.WHITE);

            JButton prev = createCalendarHeaderButton("<");
            JButton next = createCalendarHeaderButton(">");
            prev.addActionListener(e -> {
                displayedMonth = displayedMonth.minusMonths(1);
                refreshCalendar();
            });
            next.addActionListener(e -> {
                displayedMonth = displayedMonth.plusMonths(1);
                refreshCalendar();
            });

            monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            monthLabel.setForeground(TEXT);

            JPanel header = new JPanel(new BorderLayout(8, 0));
            header.setOpaque(false);
            header.add(prev, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);

            daysPanel.setOpaque(false);

            root.add(header, BorderLayout.NORTH);
            root.add(daysPanel, BorderLayout.CENTER);

            popup.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
            popup.add(root);
        }

        private void show() {
            selectedDate = parseDate(target.getText().trim());
            displayedMonth = selectedDate != null ? YearMonth.from(selectedDate) : YearMonth.now();
            refreshCalendar();
            popup.show(target, 0, target.getHeight());
        }

        private void refreshCalendar() {
            monthLabel.setText(displayedMonth.getMonth().name().substring(0, 1)
                    + displayedMonth.getMonth().name().substring(1).toLowerCase(Locale.ROOT)
                    + " "
                    + displayedMonth.getYear());

            daysPanel.removeAll();
            String[] weekdayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String name : weekdayNames) {
                JLabel weekday = new JLabel(name, SwingConstants.CENTER);
                weekday.setFont(new Font("Segoe UI", Font.BOLD, 11));
                weekday.setForeground(MUTED);
                daysPanel.add(weekday);
            }

            int firstDayOffset = displayedMonth.atDay(1).getDayOfWeek().getValue() - 1;
            for (int i = 0; i < firstDayOffset; i++) {
                daysPanel.add(new JLabel(""));
            }

            int lengthOfMonth = displayedMonth.lengthOfMonth();
            for (int day = 1; day <= lengthOfMonth; day++) {
                LocalDate date = displayedMonth.atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFocusPainted(false);
                dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dayButton.setOpaque(true);
                dayButton.setContentAreaFilled(true);
                dayButton.setBorderPainted(true);
                dayButton.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(TEXT);

                if (selectedDate != null && selectedDate.equals(date)) {
                    dayButton.setBackground(PRIMARY);
                    dayButton.setForeground(Color.WHITE);
                }

                dayButton.addActionListener(e -> {
                    target.setText(date.format(DATE_PICKER_FORMATTER));
                    popup.setVisible(false);
                });
                daysPanel.add(dayButton);
            }

            daysPanel.revalidate();
            daysPanel.repaint();
            popup.pack();
        }

        private JButton createCalendarHeaderButton(String text) {
            JButton button = new JButton(text);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
            button.setBackground(PANEL_ALT);
            button.setForeground(TEXT);
            button.setFont(new Font("Dialog", Font.BOLD, 12));
            return button;
        }

        private LocalDate parseDate(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(value, DATE_PICKER_FORMATTER);
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }
}
