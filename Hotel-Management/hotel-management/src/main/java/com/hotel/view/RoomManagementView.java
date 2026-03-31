package com.hotel.view;

import com.hotel.controller.RoomController;
import com.hotel.controller.RoomTypeController;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomManagementView extends JPanel {
    private final RoomController roomController;
    private final RoomTypeController roomTypeController;

    private JTextField txtRoomTypeName;
    private JTextField txtRoomTypePrice;
    private JTextField txtRoomTypeDescription;
    private DefaultTableModel roomTypeTableModel;
    private JTable tblRoomTypes;

    private JTextField txtRoomNumber;
    private JComboBox<String> roomTypeCombo;
    private JComboBox<String> roomStatusCombo;
    private DefaultTableModel roomTableModel;
    private JTable tblRooms;

    public RoomManagementView(RoomController roomController, RoomTypeController roomTypeController) {
        this.roomController = roomController;
        this.roomTypeController = roomTypeController;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel header = UiStyles.createCardPanel(new BorderLayout(0, 4), 18, 20, 18, 20);
        header.add(UiStyles.createTitle("Room Management"), BorderLayout.NORTH);
        header.add(UiStyles.createHint("Create room types first, then create rooms that use those types."), BorderLayout.CENTER);

        JPanel roomTypeForm = buildRoomTypeForm();
        JPanel roomForm = buildRoomForm();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomTypeForm, roomForm);
        splitPane.setResizeWeight(0.48);
        splitPane.setBorder(null);

        add(header, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel buildRoomTypeForm() {
        JPanel panel = UiStyles.createCardPanel(new BorderLayout(16, 16), 20, 20, 20, 20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtRoomTypeName = new JTextField(15);
        txtRoomTypePrice = new JTextField(15);
        txtRoomTypeDescription = new JTextField(15);
        UiStyles.styleTextField(txtRoomTypeName);
        UiStyles.styleTextField(txtRoomTypePrice);
        UiStyles.styleTextField(txtRoomTypeDescription);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(UiStyles.createSectionTitle("Room Types"), gbc);

        addField(form, gbc, 1, "Name", txtRoomTypeName, true);
        addField(form, gbc, 3, "Price / Night", txtRoomTypePrice, true);
        addField(form, gbc, 5, "Description", txtRoomTypeDescription, false);

        JButton btnAdd = UiStyles.createButton("ADD TYPE", UiStyles.SUCCESS);
        JButton btnEdit = UiStyles.createButton("EDIT TYPE", UiStyles.PRIMARY);
        JButton btnDelete = UiStyles.createButton("DELETE TYPE", UiStyles.DANGER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 8, 0, 8);
        form.add(UiStyles.createRequiredNoteLabel(), gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(actionPanel, gbc);

        roomTypeTableModel = new DefaultTableModel(new String[]{"Id", "Name", "Price", "Description"}, 0);
        tblRoomTypes = new JTable(roomTypeTableModel);
        UiStyles.styleTable(tblRoomTypes);
        tblRoomTypes.getColumnModel().getColumn(0).setMinWidth(0);
        tblRoomTypes.getColumnModel().getColumn(0).setMaxWidth(0);
        tblRoomTypes.getColumnModel().getColumn(0).setWidth(0);
        tblRoomTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRoomTypes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateRoomTypeSelection();
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(UiStyles.wrapTable(tblRoomTypes, "Room Type List"), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddRoomType());
        btnEdit.addActionListener(e -> handleEditRoomType());
        btnDelete.addActionListener(e -> handleDeleteRoomType());

        return panel;
    }

    private JPanel buildRoomForm() {
        JPanel panel = UiStyles.createCardPanel(new BorderLayout(16, 16), 20, 20, 20, 20);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtRoomNumber = new JTextField(15);
        roomTypeCombo = new JComboBox<>();
        roomStatusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        UiStyles.styleTextField(txtRoomNumber);
        UiStyles.styleComboBox(roomTypeCombo);
        UiStyles.styleComboBox(roomStatusCombo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(UiStyles.createSectionTitle("Rooms"), gbc);

        addField(form, gbc, 1, "Room Number", txtRoomNumber, true);
        addField(form, gbc, 3, "Room Type", roomTypeCombo, true);
        addField(form, gbc, 5, "Status", roomStatusCombo, false);

        JButton btnAdd = UiStyles.createButton("ADD ROOM", UiStyles.SUCCESS);
        JButton btnEdit = UiStyles.createButton("EDIT ROOM", UiStyles.PRIMARY);
        JButton btnDelete = UiStyles.createButton("DELETE ROOM", UiStyles.DANGER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 8, 0, 8);
        form.add(UiStyles.createRequiredNoteLabel(), gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(actionPanel, gbc);

        roomTableModel = new DefaultTableModel(new String[]{"Id", "Room Number", "Type", "Status"}, 0);
        tblRooms = new JTable(roomTableModel);
        UiStyles.styleTable(tblRooms);
        tblRooms.getColumnModel().getColumn(0).setMinWidth(0);
        tblRooms.getColumnModel().getColumn(0).setMaxWidth(0);
        tblRooms.getColumnModel().getColumn(0).setWidth(0);
        tblRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRooms.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateRoomSelection();
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(UiStyles.wrapTable(tblRooms, "Room List"), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddRoom());
        btnEdit.addActionListener(e -> handleEditRoom());
        btnDelete.addActionListener(e -> handleDeleteRoom());

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component component, boolean required) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel title = required ? UiStyles.createRequiredSectionTitle(label) : UiStyles.createSectionTitle(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(title, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(component, gbc);
    }

    public void refreshData() {
        refreshRoomTypeTable();
        refreshRoomTypeCombo();
        refreshRoomTable();
    }

    private void handleAddRoomType() {
        try {
            String name = txtRoomTypeName.getText().trim();
            String priceText = txtRoomTypePrice.getText().trim();
            String description = txtRoomTypeDescription.getText().trim();
            if (name.isEmpty() || priceText.isEmpty()) {
                UiMessages.showWarning(this, "Room type name and price are required.");
                return;
            }

            RoomType roomType = new RoomType();
            roomType.setName(name);
            roomType.setPricePerNight(Double.parseDouble(priceText));
            roomType.setDescription(description);
            roomTypeController.addRoomType(roomType);
            clearRoomTypeForm();
            refreshData();
            UiMessages.showInfo(this, "Room type added successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void handleEditRoomType() {
        int selected = tblRoomTypes.getSelectedRow();
        if (selected < 0) {
            UiMessages.showWarning(this, "Please select a room type to edit.");
            return;
        }
        try {
            Long id = (Long) roomTypeTableModel.getValueAt(selected, 0);
            RoomType roomType = roomTypeController.getRoomTypeById(id);
            if (roomType == null) {
                return;
            }
            roomType.setName(txtRoomTypeName.getText().trim());
            roomType.setPricePerNight(Double.parseDouble(txtRoomTypePrice.getText().trim()));
            roomType.setDescription(txtRoomTypeDescription.getText().trim());
            roomTypeController.updateRoomType(roomType);
            clearRoomTypeForm();
            refreshData();
            UiMessages.showInfo(this, "Room type updated successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void handleDeleteRoomType() {
        int selected = tblRoomTypes.getSelectedRow();
        if (selected < 0) {
            UiMessages.showWarning(this, "Please select a room type to delete.");
            return;
        }
        if (!UiMessages.confirm(this, "Delete Room Type", "Are you sure you want to delete the selected room type?")) {
            return;
        }
        Long id = (Long) roomTypeTableModel.getValueAt(selected, 0);
        try {
            roomTypeController.deleteRoomType(id);
            clearRoomTypeForm();
            refreshData();
            UiMessages.showInfo(this, "Room type deleted successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void handleAddRoom() {
        try {
            String roomNumber = txtRoomNumber.getText().trim();
            String roomTypeName = (String) roomTypeCombo.getSelectedItem();
            String status = (String) roomStatusCombo.getSelectedItem();
            if (roomNumber.isEmpty() || roomTypeName == null) {
                UiMessages.showWarning(this, "Room number and room type are required.");
                return;
            }

            RoomType roomType = roomTypeController.getRoomTypeByName(roomTypeName);
            if (roomType == null) {
                UiMessages.showWarning(this, "Selected room type not found.");
                return;
            }

            Room room = new Room();
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomType);
            room.setStatus(status);
            roomController.addRoom(room);
            clearRoomForm();
            refreshData();
            UiMessages.showInfo(this, "Room added successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void handleEditRoom() {
        int selected = tblRooms.getSelectedRow();
        if (selected < 0) {
            UiMessages.showWarning(this, "Please select a room to edit.");
            return;
        }
        try {
            Long id = (Long) roomTableModel.getValueAt(selected, 0);
            Room room = roomController.getRoomById(id);
            if (room == null) {
                return;
            }
            RoomType roomType = roomTypeController.getRoomTypeByName((String) roomTypeCombo.getSelectedItem());
            if (roomType == null) {
                UiMessages.showWarning(this, "Selected room type not found.");
                return;
            }
            room.setRoomNumber(txtRoomNumber.getText().trim());
            room.setRoomType(roomType);
            room.setStatus((String) roomStatusCombo.getSelectedItem());
            roomController.updateRoom(room);
            clearRoomForm();
            refreshData();
            UiMessages.showInfo(this, "Room updated successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void handleDeleteRoom() {
        int selected = tblRooms.getSelectedRow();
        if (selected < 0) {
            UiMessages.showWarning(this, "Please select a room to delete.");
            return;
        }
        if (!UiMessages.confirm(this, "Delete Room", "Are you sure you want to delete the selected room?")) {
            return;
        }
        Long id = (Long) roomTableModel.getValueAt(selected, 0);
        try {
            roomController.deleteRoom(id);
            clearRoomForm();
            refreshData();
            UiMessages.showInfo(this, "Room deleted successfully.");
        } catch (Exception ex) {
            UiMessages.showError(this, ex.getMessage());
        }
    }

    private void refreshRoomTypeTable() {
        roomTypeTableModel.setRowCount(0);
        List<RoomType> roomTypes = roomTypeController.getAllRoomTypes();
        for (RoomType roomType : roomTypes) {
            roomTypeTableModel.addRow(new Object[]{roomType.getId(), roomType.getName(), roomType.getPricePerNight(), roomType.getDescription()});
        }
    }

    private void refreshRoomTypeCombo() {
        roomTypeCombo.removeAllItems();
        List<RoomType> roomTypes = roomTypeController.getAllRoomTypes();
        for (RoomType roomType : roomTypes) {
            roomTypeCombo.addItem(roomType.getName());
        }
    }

    private void refreshRoomTable() {
        roomTableModel.setRowCount(0);
        List<Room> rooms = roomController.getAllRooms();
        for (Room room : rooms) {
            String roomTypeName = room.getRoomType() == null ? "-" : room.getRoomType().getName();
            roomTableModel.addRow(new Object[]{room.getId(), room.getRoomNumber(), roomTypeName, room.getStatus()});
        }
    }

    private void populateRoomTypeSelection() {
        int selected = tblRoomTypes.getSelectedRow();
        if (selected < 0) {
            return;
        }
        txtRoomTypeName.setText(String.valueOf(roomTypeTableModel.getValueAt(selected, 1)));
        txtRoomTypePrice.setText(String.valueOf(roomTypeTableModel.getValueAt(selected, 2)));
        txtRoomTypeDescription.setText(String.valueOf(roomTypeTableModel.getValueAt(selected, 3)));
    }

    private void populateRoomSelection() {
        int selected = tblRooms.getSelectedRow();
        if (selected < 0) {
            return;
        }
        txtRoomNumber.setText(String.valueOf(roomTableModel.getValueAt(selected, 1)));
        roomTypeCombo.setSelectedItem(roomTableModel.getValueAt(selected, 2));
        roomStatusCombo.setSelectedItem(roomTableModel.getValueAt(selected, 3));
    }

    private void clearRoomTypeForm() {
        txtRoomTypeName.setText("");
        txtRoomTypePrice.setText("");
        txtRoomTypeDescription.setText("");
        tblRoomTypes.clearSelection();
    }

    private void clearRoomForm() {
        txtRoomNumber.setText("");
        if (roomTypeCombo.getItemCount() > 0) {
            roomTypeCombo.setSelectedIndex(0);
        }
        roomStatusCombo.setSelectedItem("AVAILABLE");
        tblRooms.clearSelection();
    }
}
