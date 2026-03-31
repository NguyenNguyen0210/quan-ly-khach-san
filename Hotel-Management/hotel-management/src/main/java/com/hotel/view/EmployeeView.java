package com.hotel.view;

import com.hotel.controller.EmployeeController;
import com.hotel.entity.Employee;
import com.hotel.security.EmployeeRoles;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeView extends JPanel {
    private final EmployeeController controller;
    private final JTextField txtName;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JComboBox<String> roleCombo;
    private final JTextField txtSalary;
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnAdd;
    private final JButton btnEdit;
    private final JButton btnDelete;
    private final JLabel lblStatus;

    public EmployeeView(EmployeeController controller) {
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
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        roleCombo = new JComboBox<>(new String[]{
                EmployeeRoles.toDisplayName(EmployeeRoles.RECEPTIONIST),
                EmployeeRoles.toDisplayName(EmployeeRoles.MANAGER),
                EmployeeRoles.toDisplayName(EmployeeRoles.SERVICE_STAFF)
        });
        txtSalary = new JTextField(15);
        UiStyles.styleTextField(txtName);
        UiStyles.styleTextField(txtUsername);
        UiStyles.styleTextField(txtPassword);
        UiStyles.styleComboBox(roleCombo);
        UiStyles.styleTextField(txtSalary);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(UiStyles.createTitle("Employee"), gbc);

        gbc.gridy = 1;
        leftPanel.add(UiStyles.createHint("Maintain team members, roles and salary information."), gbc);

        addField(leftPanel, gbc, 2, "Name", txtName);
        addField(leftPanel, gbc, 4, "Username", txtUsername);
        addField(leftPanel, gbc, 6, "Password", txtPassword);
        addField(leftPanel, gbc, 8, "Role", roleCombo);
        addField(leftPanel, gbc, 10, "Salary", txtSalary);

        btnAdd = UiStyles.createButton("ADD", UiStyles.SUCCESS);
        btnEdit = UiStyles.createButton("EDIT", UiStyles.PRIMARY);
        btnDelete = UiStyles.createButton("DELETE", UiStyles.DANGER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        gbc.gridy = 12;
        gbc.insets = new Insets(18, 8, 0, 8);
        leftPanel.add(btnPanel, gbc);

        lblStatus = UiStyles.createStatusLabel();
        gbc.gridy = 13;
        gbc.insets = new Insets(12, 8, 0, 8);
        leftPanel.add(lblStatus, gbc);

        gbc.gridy = 14;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 0, 8);
        leftPanel.add(Box.createVerticalGlue(), gbc);

        String[] columns = {"Id", "Name", "Username", "Role", "Salary"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UiStyles.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = UiStyles.wrapTable(table, "Employee List");

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddEmployee());
        btnEdit.addActionListener(e -> handleEditEmployee());
        btnDelete.addActionListener(e -> handleDeleteEmployee());
        attachValidationListeners();

        refreshTable();
        validateFormState();
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
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

    private void handleAddEmployee() {
        try {
            String name = txtName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String role = normalizeRole((String) roleCombo.getSelectedItem());
            String salaryText = txtSalary.getText().trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Name, username, password and role are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            Double salary = salaryText.isEmpty() ? 0.0 : Double.parseDouble(salaryText);
            Employee employee = new Employee();
            employee.setFullName(name);
            employee.setUsername(username);
            employee.setPassword(password);
            employee.setRole(role);
            employee.setSalary(salary);

            controller.addEmployee(employee);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Employee added successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Salary must be a valid number.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleEditEmployee() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select an employee to edit.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        try {
            Long id = (Long) model.getValueAt(selected, 0);
            Employee employee = controller.getEmployeeById(id);
            if (employee == null) {
                UiStyles.setStatus(lblStatus, "Selected employee not found.", UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
                return;
            }

            String name = txtName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String role = normalizeRole((String) roleCombo.getSelectedItem());
            String salaryText = txtSalary.getText().trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                UiStyles.setStatus(lblStatus, "Name, username, password and role are required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
                validateFormState();
                return;
            }

            Double salary = salaryText.isEmpty() ? 0.0 : Double.parseDouble(salaryText);
            employee.setFullName(name);
            employee.setUsername(username);
            employee.setPassword(password);
            employee.setRole(role);
            employee.setSalary(salary);
            controller.updateEmployee(employee);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Employee updated successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (NumberFormatException ex) {
            UiStyles.setStatus(lblStatus, "Salary must be a valid number.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            validateFormState();
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void handleDeleteEmployee() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            UiStyles.setStatus(lblStatus, "Please select an employee to delete.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            return;
        }
        if (!UiMessages.confirm(this, "Delete Employee", "Are you sure you want to delete the selected employee?")) {
            return;
        }
        Long id = (Long) model.getValueAt(selected, 0);
        try {
            controller.deleteEmployee(id);
            refreshTable();
            clearForm();
            UiStyles.setStatus(lblStatus, "Employee deleted successfully.", UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } catch (Exception ex) {
            UiStyles.setStatus(lblStatus, UiMessages.normalizeMessage(ex.getMessage()), UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Employee> employees = controller.getAllEmployees();
        for (Employee employee : employees) {
            model.addRow(new Object[]{
                    employee.getId(),
                    employee.getFullName(),
                    employee.getUsername(),
                    EmployeeRoles.toDisplayName(employee.getRole()),
                    employee.getSalary()
            });
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        roleCombo.setSelectedIndex(0);
        txtSalary.setText("");
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
        txtUsername.getDocument().addDocumentListener(listener);
        txtPassword.getDocument().addDocumentListener(listener);
        txtSalary.getDocument().addDocumentListener(listener);
        roleCombo.addActionListener(e -> validateFormState());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFromSelection();
                validateFormState();
            }
        });
    }

    private void validateFormState() {
        boolean hasName = !txtName.getText().trim().isEmpty();
        boolean hasUsername = !txtUsername.getText().trim().isEmpty();
        boolean hasPassword = txtPassword.getPassword().length > 0;
        boolean hasRole = roleCombo.getSelectedItem() != null;
        boolean validSalary = txtSalary.getText().trim().isEmpty();
        if (!validSalary) {
            try {
                Double.parseDouble(txtSalary.getText().trim());
                validSalary = true;
            } catch (Exception ignored) {
                validSalary = false;
            }
        }

        UiStyles.setFieldValidation(txtName, hasName);
        UiStyles.setFieldValidation(txtUsername, hasUsername);
        UiStyles.setFieldValidation(txtPassword, hasPassword);
        UiStyles.setFieldValidation(roleCombo, hasRole);
        UiStyles.setFieldValidation(txtSalary, validSalary);

        boolean formValid = hasName && hasUsername && hasPassword && hasRole && validSalary;
        boolean hasSelection = table.getSelectedRow() >= 0;

        btnAdd.setEnabled(formValid);
        btnEdit.setEnabled(formValid && hasSelection);
        btnDelete.setEnabled(hasSelection);

        if (!formValid) {
            UiStyles.setStatus(lblStatus, "Fill account information and a valid salary to manage employees.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (hasSelection) {
            UiStyles.setStatus(lblStatus, "Employee selected. You can edit or delete this record.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else {
            UiStyles.setStatus(lblStatus, "Form is ready. You can add a new employee.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }

    private void populateFromSelection() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            return;
        }
        Long id = (Long) model.getValueAt(selected, 0);
        Employee employee = controller.getEmployeeById(id);
        if (employee == null) {
            return;
        }
        txtName.setText(employee.getFullName() == null ? "" : employee.getFullName());
        txtUsername.setText(employee.getUsername() == null ? "" : employee.getUsername());
        txtPassword.setText(employee.getPassword() == null ? "" : employee.getPassword());
        roleCombo.setSelectedItem(EmployeeRoles.toDisplayName(employee.getRole()));
        txtSalary.setText(employee.getSalary() == null ? "" : String.valueOf(employee.getSalary()));
    }

    private String normalizeRole(String displayRole) {
        if (displayRole == null) {
            return "";
        }
        if (EmployeeRoles.toDisplayName(EmployeeRoles.RECEPTIONIST).equals(displayRole)) {
            return EmployeeRoles.RECEPTIONIST;
        }
        if (EmployeeRoles.toDisplayName(EmployeeRoles.MANAGER).equals(displayRole)) {
            return EmployeeRoles.MANAGER;
        }
        if (EmployeeRoles.toDisplayName(EmployeeRoles.SERVICE_STAFF).equals(displayRole)) {
            return EmployeeRoles.SERVICE_STAFF;
        }
        return displayRole.trim().toUpperCase().replace(' ', '_');
    }
}
