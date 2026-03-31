package com.hotel.view;

import com.hotel.Main;
import com.hotel.controller.BookingController;
import com.hotel.controller.RoomController;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.security.AuthSession;
import com.hotel.security.EmployeeRoles;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoomPanel extends JPanel {
    private final Main mainApp;
    private final RoomController roomController;
    private final BookingController bookingController;
    private final JPanel roomGrid;
    private final JPanel summaryPanel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color ROOM_EMPTY = new Color(220, 245, 229);
    private final Color ROOM_PENDING = new Color(255, 244, 209);
    private final Color ROOM_OCCUPIED = new Color(255, 222, 218);
    private final Color ROOM_MAINTENANCE = new Color(232, 236, 243);

    public RoomPanel(Main app, RoomController roomController, BookingController bookingController) {
        this.mainApp = app;
        this.roomController = roomController;
        this.bookingController = bookingController;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel header = UiStyles.createCardPanel(new BorderLayout(0, 4), 18, 20, 18, 20);
        header.add(UiStyles.createTitle("Room Overview"), BorderLayout.NORTH);

        summaryPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        summaryPanel.setOpaque(false);
        header.add(summaryPanel, BorderLayout.CENTER);

        roomGrid = new JPanel(new GridLayout(0, 4, 16, 16));
        roomGrid.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(roomGrid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UiStyles.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshRooms();
    }

    public void refreshRooms() {
        roomGrid.removeAll();
        List<Room> rooms = roomController.getAllRooms();
        renderSummary(rooms);
        if (rooms == null || rooms.isEmpty()) {
            roomGrid.setLayout(new GridLayout(1, 1));
            JPanel emptyPanel = UiStyles.createCardPanel(new GridBagLayout(), 32, 32, 32, 32);
            emptyPanel.add(UiStyles.createHint("No rooms found in the database."));
            roomGrid.add(emptyPanel);
        } else {
            roomGrid.setLayout(new GridLayout(0, 4, 16, 16));
            for (Room room : rooms) {
                String roomId = room.getRoomNumber() == null ? "N/A" : room.getRoomNumber();
                roomGrid.add(addRoomCard(room, roomId, getRoomColor(room)));
            }
        }
        roomGrid.revalidate();
        roomGrid.repaint();
    }

    private JPanel addRoomCard(Room room, String id, Color color) {
        Booking booking = bookingController == null ? null : bookingController.getActiveBookingForRoom(room.getId());

        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiStyles.BORDER, 1, true),
                BorderFactory.createEmptyBorder(18, 16, 18, 16)
        ));
        card.setPreferredSize(new Dimension(220, 170));

        JLabel lblId = new JLabel(id);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblId.setForeground(UiStyles.TEXT);

        JLabel statusBadge = createStatusBadge(resolveStatusText(room), color);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblId, BorderLayout.WEST);
        top.add(statusBadge, BorderLayout.EAST);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        infoPanel.setOpaque(false);
        infoPanel.add(createInfoRow("Type", room.getRoomType() != null ? room.getRoomType().getName() : "Standard"));
        infoPanel.add(createInfoRow("Room Status", formatRoomStatus(room.getStatus())));
        infoPanel.add(createInfoRow("Guest", resolveGuestName(booking)));
        infoPanel.add(createInfoRow("Stay", resolveStayText(booking)));

        card.add(top, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UiStyles.PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(18, 16, 18, 16)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UiStyles.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(18, 16, 18, 16)
                ));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e, room);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e, room);
            }
        });
        return card;
    }

    private void renderSummary(List<Room> rooms) {
        summaryPanel.removeAll();

        int total = rooms == null ? 0 : rooms.size();
        int available = 0;
        int booked = 0;
        int occupied = 0;
        int maintenance = 0;

        if (rooms != null) {
            for (Room room : rooms) {
                Booking booking = bookingController == null ? null : bookingController.getActiveBookingForRoom(room.getId());
                if (booking != null) {
                    if ("CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                        occupied++;
                        continue;
                    }
                    if ("BOOKED".equalsIgnoreCase(booking.getStatus())) {
                        booked++;
                        continue;
                    }
                }
                if ("MAINTENANCE".equalsIgnoreCase(room.getStatus())) {
                    maintenance++;
                } else {
                    available++;
                }
            }
        }

        summaryPanel.add(createSummaryCard("Total Rooms", String.valueOf(total), UiStyles.PRIMARY, new Color(232, 240, 254)));
        summaryPanel.add(createSummaryCard("Available", String.valueOf(available), UiStyles.SUCCESS, new Color(228, 245, 234)));
        summaryPanel.add(createSummaryCard("Booked", String.valueOf(booked), UiStyles.WARNING, new Color(255, 246, 224)));
        summaryPanel.add(createSummaryCard("Occupied", String.valueOf(occupied), UiStyles.DANGER, new Color(252, 232, 230)));

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel createSummaryCard(String title, String value, Color accent, Color background) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setOpaque(true);
        card.setBackground(background);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiStyles.BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(UiStyles.MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createStatusBadge(String text, Color background) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(UiStyles.TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return label;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(UiStyles.MUTED);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueComp.setForeground(UiStyles.TEXT);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);
        return row;
    }

    private String resolveGuestName(Booking booking) {
        if (booking == null || booking.getCustomer() == null || booking.getCustomer().getFullName() == null || booking.getCustomer().getFullName().isBlank()) {
            return "No guest";
        }
        return booking.getCustomer().getFullName();
    }

    private String resolveStayText(Booking booking) {
        if (booking == null || booking.getCheckInDate() == null) {
            return "Open";
        }
        String checkIn = booking.getCheckInDate().format(dateFormatter);
        if (booking.getCheckOutDate() == null) {
            return checkIn;
        }
        return checkIn + " - " + booking.getCheckOutDate().format(dateFormatter);
    }

    private String formatRoomStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Available";
        }
        return status.replace('_', ' ');
    }

    private String resolveStatusText(Room room) {
        if (room == null) {
            return "Available";
        }
        Booking booking = bookingController == null ? null : bookingController.getActiveBookingForRoom(room.getId());
        if (booking != null) {
            if ("CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                return "Occupied";
            }
            if ("BOOKED".equalsIgnoreCase(booking.getStatus())) {
                return "Booked";
            }
        }
        String status = room.getStatus();
        if (status == null || status.isBlank()) {
            return "Available";
        }
        return status.replace('_', ' ');
    }

    private Color getColorForStatus(String status) {
        if (status == null) {
            return ROOM_EMPTY;
        }
        return switch (status.trim().toUpperCase()) {
            case "OCCUPIED" -> ROOM_OCCUPIED;
            case "MAINTENANCE" -> ROOM_MAINTENANCE;
            case "PENDING", "CHECKED_OUT" -> ROOM_PENDING;
            default -> ROOM_EMPTY;
        };
    }

    private void maybeShowPopup(MouseEvent e, Room room) {
        if (!e.isPopupTrigger()) {
            return;
        }
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(UiStyles.BORDER, 1, true));

        String roomId = room.getRoomNumber() == null ? "N/A" : room.getRoomNumber();
        Booking booking = bookingController == null ? null : bookingController.getActiveBookingForRoom(room.getId());
        boolean available = isRoomAvailable(room, booking);

        if (available) {
            if (AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER)) {
                JMenuItem bookingItem = new JMenuItem("Booking");
                bookingItem.addActionListener(ev -> mainApp.showBookCard(roomId));
                popup.add(bookingItem);
            }
        } else {
            if (booking != null && "BOOKED".equalsIgnoreCase(booking.getStatus())
                    && AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER)) {
                JMenuItem checkInItem = new JMenuItem("Check-in");
                checkInItem.addActionListener(ev -> {
                    try {
                        bookingController.checkIn(booking.getId());
                        refreshRooms();
                        UiMessages.showInfo(this, "Guest checked in successfully.");
                    } catch (Exception ex) {
                        UiMessages.showError(this, ex.getMessage());
                    }
                });
                popup.add(checkInItem);
            }
            if (booking != null && "CHECKED_IN".equalsIgnoreCase(booking.getStatus())
                    && AuthSession.hasAnyRole(EmployeeRoles.RECEPTIONIST, EmployeeRoles.MANAGER)) {
                JMenuItem checkOutItem = new JMenuItem("Check-out");
                checkOutItem.addActionListener(ev -> mainApp.showCheckoutCard(booking.getId()));
                popup.add(checkOutItem);
            }

            if (booking != null && "CHECKED_IN".equalsIgnoreCase(booking.getStatus())
                    && AuthSession.hasAnyRole(EmployeeRoles.SERVICE_STAFF, EmployeeRoles.MANAGER)) {
                JMenuItem addServiceItem = new JMenuItem("Add Service");
                addServiceItem.addActionListener(ev -> mainApp.showAddServiceCard(booking.getId()));
                popup.add(addServiceItem);
            }
        }

        if (popup.getComponentCount() == 0) {
            JMenuItem noActionItem = new JMenuItem("No actions available");
            noActionItem.setEnabled(false);
            popup.add(noActionItem);
        }

        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private boolean isRoomAvailable(Room room, Booking booking) {
        if (room == null) {
            return false;
        }
        if (booking != null) {
            if ("CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                return false;
            }
            if ("BOOKED".equalsIgnoreCase(booking.getStatus())) {
                return false;
            }
        }
        String status = room.getStatus();
        return status == null || status.isBlank() || "AVAILABLE".equalsIgnoreCase(status.trim());
    }

    private Color getRoomColor(Room room) {
        if (room == null) {
            return ROOM_EMPTY;
        }
        Booking booking = bookingController == null ? null : bookingController.getActiveBookingForRoom(room.getId());
        if (booking != null) {
            if ("CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
                return ROOM_OCCUPIED;
            }
            if ("BOOKED".equalsIgnoreCase(booking.getStatus())) {
                return ROOM_PENDING;
            }
        }
        return getColorForStatus(room.getStatus());
    }
}
