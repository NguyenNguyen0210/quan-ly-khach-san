package com.hotel.view;

import com.hotel.controller.CustomerController;
import com.hotel.entity.Customer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerView extends JPanel {
    private final CustomerController controller;
    private final JTextField txtName;
    private final JTextField txtPhone;
    private final JTextField txtEmail;
    private final JTextField txtIdCard;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd;
    private final JButton btnEdit;
    private final JButton btnDelete;
    private final JLabel lblStatus;

    public CustomerView(CustomerController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        UiStyles.stylePage(this);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        JPanel form = UiStyles.createCardPanel(new GridBagLayout(), 22, 22, 22, 22);
        form.setPreferredSize(new Dimension(360, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(15);
        txtPhone = new JTextField(15);
        txtEmail = new JTextField(15);
        txtIdCard = new JTextField(15);
        UiStyles.styleTextField(txtName);
        UiStyles.styleTextField(txtPhone);
        UiStyles.styleTextField(txtEmail);
        UiStyles.styleTextField(txtIdCard);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(UiStyles.createTitle("Customer"), gbc);

        gbc.gridy = 1;
        form.add(UiStyles.createHint("Create, update and manage guest contact information."), gbc);

        addField(form, gbc, 2, "Full Name", txtName);
        addField(form, gbc, 4, "Phone", txtPhone);
        addField(form, gbc, 6, "Email", txtEmail);
        addField(form, gbc, 8, "ID Card", txtIdCard);

        btnAdd = UiStyles.createButton("ADD", UiStyles.SUCCESS);
        btnEdit = UiStyles.createButton("EDIT", UiStyles.PRIMARY);
        btnDelete = UiStyles.createButton("DELETE", UiStyles.DANGER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 0, 8);
        form.add(actions, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 11;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(lblStatus, gbc);

        String[] cols = {"Id", "ID Card", "Name", "Phone", "Email"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        UiStyles.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    populateSelectedCustomer();
                }
            }
        });
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = UiStyles.wrapTable(table, "Customer List");

        contentPanel.add(form, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddCustomer());
        btnEdit.addActionListener(e -> handleEditCustomer());
        btnDelete.addActionListener(e -> handleDeleteCustomer());
        attachValidationListeners();

        refreshTable();
        validateFormState();
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel title = UiStyles.createSectionTitle(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(title, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(field, gbc);
    }

    private void handleAddCustomer() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String idCard = txtIdCard.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Name, phone and ID card are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            Customer customer = new Customer();
            customer.setFullName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setIdCard(idCard);

            controller.registerCustomer(customer);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Customer added successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleEditCustomer() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select a customer to edit.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        try {
            Long id = (Long) model.getValueAt(selected, 0);
            Customer customer = controller.getCustomerById(id);
            if (customer == null) {
                UiStyles.setStatus(lblStatus, "Selected customer not found.", UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
                return;
            }

            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String idCard = txtIdCard.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Name, phone and ID card are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            customer.setFullName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setIdCard(idCard);
            controller.updateCustomer(customer);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Customer updated successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleDeleteCustomer() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select a customer to delete.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        if (!UiMessages.confirm(this, "Delete Customer", "Are you sure you want to delete the selected customer?")) {
            return;
        }
        Long id = (Long) model.getValueAt(selected, 0);
        try {
            controller.deleteCustomer(id);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Customer deleted successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Customer> customers = controller.getAllCustomers();
        for (Customer customer : customers) {
            model.addRow(new Object[]{customer.getId(), customer.getIdCard(), customer.getFullName(), customer.getPhone(), customer.getEmail()});
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtIdCard.setText("");
        table.clearSelection();
        validateFormState();
    }

    private void populateSelectedCustomer() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return;
        }
        txtIdCard.setText((String) model.getValueAt(selected, 1));
        txtName.setText((String) model.getValueAt(selected, 2));
        txtPhone.setText((String) model.getValueAt(selected, 3));
        txtEmail.setText((String) model.getValueAt(selected, 4));
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
        txtPhone.getDocument().addDocumentListener(listener);
        txtEmail.getDocument().addDocumentListener(listener);
        txtIdCard.getDocument().addDocumentListener(listener);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                validateFormState();
            }
        });
    }

    private void validateFormState() {
        boolean hasName = !txtName.getText().trim().isEmpty();
        boolean hasPhone = !txtPhone.getText().trim().isEmpty();
        boolean hasIdCard = !txtIdCard.getText().trim().isEmpty();

        UiStyles.setFieldValidation(txtName, hasName);
        UiStyles.setFieldValidation(txtPhone, hasPhone);
        UiStyles.setFieldValidation(txtIdCard, hasIdCard);
        UiStyles.setFieldValidation(txtEmail, true);

        boolean formValid = hasName && hasPhone && hasIdCard;
        boolean hasSelection = table.getSelectedRow() >= 0;

        btnAdd.setEnabled(formValid);
        btnEdit.setEnabled(formValid && hasSelection);
        btnDelete.setEnabled(hasSelection);

        if (!formValid) {
            UiStyles.setStatus(lblStatus, "Fill name, phone and ID card to manage customers.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (hasSelection) {
            UiStyles.setStatus(lblStatus, "Customer selected. You can edit or delete this record.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else {
            UiStyles.setStatus(lblStatus, "Form is ready. You can add a new customer.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }
}
