package com.hotel.view;

import com.hotel.controller.ServiceCatalogController;
import com.hotel.entity.Service;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServiceView extends JPanel {

    private final ServiceCatalogController controller;
    private final JTextField txtName;
    private final JTextField txtPrice;
    private final JTextField txtQuantity;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd;
    private final JButton btnEdit;
    private final JButton btnDelete;
    private final JLabel lblStatus;

    public ServiceView(ServiceCatalogController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        JPanel leftPanel = UiStyles.createCardPanel(new GridBagLayout(), 22, 22, 22, 22);
        leftPanel.setPreferredSize(new Dimension(360, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtName = new JTextField(15);
        txtPrice = new JTextField(15);
        txtQuantity = new JTextField(15);
        UiStyles.styleTextField(txtName);
        UiStyles.styleTextField(txtPrice);
        UiStyles.styleTextField(txtQuantity);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 14, 8, 14);
        leftPanel.add(UiStyles.createTitle("Service"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 14, 8, 14);
        leftPanel.add(UiStyles.createHint("Manage service catalog, price and available quantity."), gbc);

        addField(leftPanel, gbc, 2, "Service Name", txtName, true);
        addField(leftPanel, gbc, 4, "Price", txtPrice, true);
        addField(leftPanel, gbc, 6, "Quantity", txtQuantity, true);

        btnAdd = UiStyles.createButton("ADD", UiStyles.SUCCESS);
        btnEdit = UiStyles.createButton("EDIT", UiStyles.PRIMARY);
        btnDelete = UiStyles.createButton("DELETE", UiStyles.DANGER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        gbc.gridy = 8;
        gbc.insets = new Insets(8, 14, 0, 14);
        leftPanel.add(UiStyles.createRequiredNoteLabel(), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(12, 8, 0, 8);
        leftPanel.add(btnPanel, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 10;
        gbc.insets = new Insets(12, 8, 0, 8);
        leftPanel.add(lblStatus, gbc);

        gbc.gridy = 11;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 0, 8);
        leftPanel.add(Box.createVerticalGlue(), gbc);

        String[] columns = {"Id", "Service Name", "Price", "Quantity"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UiStyles.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelection();
            }
        });

        JScrollPane scrollPane = UiStyles.wrapTable(table, "Service List");

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddService());
        btnEdit.addActionListener(e -> handleEditService());
        btnDelete.addActionListener(e -> handleDeleteService());
        attachValidationListeners();

        refreshTable();
        validateFormState();
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field, boolean required) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 14, 2, 14);
        JLabel title = required ? UiStyles.createRequiredSectionTitle(label) : UiStyles.createSectionTitle(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(title, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 14, 8, 14);
        panel.add(field, gbc);
    }

    private void handleAddService() {
        try {
            String name = txtName.getText().trim();
            String priceText = txtPrice.getText().trim();
            String quantityText = txtQuantity.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Service name, price and quantity are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            Service service = new Service();
            service.setName(name);
            service.setPrice(price);
            service.setQuantity(quantity);

            controller.addService(service);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Service added successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Price and quantity must be numeric.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleEditService() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select a service to edit.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        try {
            Long id = (Long) model.getValueAt(selected, 0);
            Service service = controller.getServiceById(id);
            if (service == null) {
                UiStyles.setStatus(lblStatus, "Selected service not found.", UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
                return;
            }

            String name = txtName.getText().trim();
            String priceText = txtPrice.getText().trim();
            String quantityText = txtQuantity.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Service name, price and quantity are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            service.setName(name);
            service.setPrice(price);
            service.setQuantity(quantity);
            controller.updateService(service);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Service updated successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Price and quantity must be numeric.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleDeleteService() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select a service to delete.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        if (!UiMessages.confirm(this, "Delete Service", "Are you sure you want to delete the selected service?")) {
            return;
        }
        Long id = (Long) model.getValueAt(selected, 0);
        try {
            controller.deleteService(id);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Service deleted successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Service> services = controller.getAllServices();
        for (Service service : services) {
            model.addRow(new Object[]{service.getId(), service.getName(), service.getPrice(), service.getQuantity()});
        }
    }

    private void populateFieldsFromSelection() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return;
        }
        txtName.setText((String) model.getValueAt(selected, 1));
        txtPrice.setText(String.valueOf(model.getValueAt(selected, 2)));
        txtQuantity.setText(String.valueOf(model.getValueAt(selected, 3)));
    }

    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        table.clearSelection();
        validateFormState();
    }

    private void attachValidationListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateFormState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateFormState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateFormState();
            }
        };
        txtName.getDocument().addDocumentListener(listener);
        txtPrice.getDocument().addDocumentListener(listener);
        txtQuantity.getDocument().addDocumentListener(listener);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validateFormState();
            }
        });
    }

    private void validateFormState() {
        boolean hasName = !txtName.getText().trim().isEmpty();
        boolean validPrice = false;
        boolean validQuantity = false;
        try {
            validPrice = !txtPrice.getText().trim().isEmpty() && Double.parseDouble(txtPrice.getText().trim()) >= 0;
        } catch (Exception ignored) {
            validPrice = false;
        }
        try {
            validQuantity = !txtQuantity.getText().trim().isEmpty() && Integer.parseInt(txtQuantity.getText().trim()) >= 0;
        } catch (Exception ignored) {
            validQuantity = false;
        }

        UiStyles.setFieldValidation(txtName, hasName);
        UiStyles.setFieldValidation(txtPrice, validPrice || txtPrice.getText().trim().isEmpty());
        UiStyles.setFieldValidation(txtQuantity, validQuantity || txtQuantity.getText().trim().isEmpty());

        boolean formValid = hasName && validPrice && validQuantity;
        boolean hasSelection = table.getSelectedRow() >= 0;

        btnAdd.setEnabled(formValid);
        btnEdit.setEnabled(formValid && hasSelection);
        btnDelete.setEnabled(hasSelection);

        if (!formValid) {
            UiStyles.setStatus(lblStatus, "Enter service name, valid price and quantity to continue.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (hasSelection) {
            UiStyles.setStatus(lblStatus, "Service selected. You can edit or delete this record.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else {
            UiStyles.setStatus(lblStatus, "Form is ready. You can add a new service.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }
}
