package com.hotel.view;

import com.hotel.controller.EmployeeController;
import com.hotel.entity.Employee;
import com.hotel.security.EmployeeRoles;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class LoginDialog extends JDialog {
    private final EmployeeController employeeController;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JCheckBox chkShowPassword;
    private final JButton btnLogin;
    private final JButton btnExit;
    private final JLabel lblStatus;
    private final char defaultEchoChar;
    private Employee authenticatedEmployee;

    public LoginDialog(Frame owner, EmployeeController employeeController) {
        super(owner, "Form Login", true);
        this.employeeController = employeeController;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(UiStyles.BACKGROUND);
        shell.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Add image panel on the left
        JPanel imagePanel = createImagePanel();
        shell.add(imagePanel, BorderLayout.WEST);

        JPanel content = UiStyles.createCardPanel(new GridBagLayout(), 24, 24, 24, 24);
        content.setPreferredSize(new Dimension(500, 410));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtUsername = new JTextField(18);
        txtPassword = new JPasswordField(18);
        defaultEchoChar = txtPassword.getEchoChar();
        UiStyles.styleTextField(txtUsername);
        UiStyles.styleTextField(txtPassword);

        chkShowPassword = new JCheckBox("Show password");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setForeground(UiStyles.MUTED);

        btnExit = UiStyles.createButton("EXIT", UiStyles.DANGER);
        btnLogin = UiStyles.createButton("LOGIN", UiStyles.PRIMARY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(UiStyles.createTitle("Sign In"), gbc);

        gbc.gridy = 1;
        content.add(UiStyles.createHint("Sign in with an employee account to continue using the system."), gbc);

        addField(content, gbc, 2, "Username", withIconField(txtUsername, createUserIcon()));
        addField(content, gbc, 4, "Password", withIconField(txtPassword, createLockIcon()));

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 8, 4, 8);
        content.add(chkShowPassword, gbc);

        JPanel helpPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        helpPanel.setOpaque(false);
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 8, 0, 8);
        content.add(helpPanel, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnExit);
        actions.add(btnLogin);
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 8, 0, 8);
        content.add(UiStyles.createRequiredNoteLabel(), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(12, 8, 0, 8);
        content.add(actions, gbc);

        lblStatus = UiStyles.createStatusLabel();
        UiStyles.setStatus(lblStatus, "Enter your username and password to sign in.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        gbc.gridy = 10;
        gbc.insets = new Insets(12, 8, 0, 8);
        content.add(lblStatus, gbc);

        shell.add(content, BorderLayout.CENTER);
        setContentPane(shell);
        pack();
        setResizable(false);
        setAlwaysOnTop(true);
        setLocationRelativeTo(owner != null && owner.isShowing() ? owner : null);

        attachListeners();
        validateForm();
        SwingUtilities.invokeLater(() -> {
            toFront();
            requestFocus();
            txtUsername.requestFocusInWindow();
        });
    }

    public Employee getAuthenticatedEmployee() {
        return authenticatedEmployee;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 2, 8);
        panel.add(createLoginSectionTitle(label), gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 8, 8, 8);
        panel.add(field, gbc);
    }

    private JLabel createLoginSectionTitle(String text) {
        JLabel label = UiStyles.createSectionTitle(text);
        label.setText(text + " *");
        label.setIcon(null);
        label.setIconTextGap(0);
        return label;
    }

    private JComponent withIconField(JComponent field, Icon icon) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 0));
        wrapper.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setPreferredSize(new Dimension(20, 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        wrapper.add(iconLabel, BorderLayout.WEST);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private Icon createUserIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiStyles.MUTED);
        g2.fillOval(5, 1, 6, 6);
        g2.fill(new RoundRectangle2D.Double(3, 8, 10, 7, 7, 7));
        g2.dispose();
        return new ImageIcon(image);
    }

    private Icon createLockIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiStyles.MUTED);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(4, 2, 8, 8, 6, 6);
        g2.fillRoundRect(3, 8, 10, 7, 2, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private void attachListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateForm();
            }
        };
        txtUsername.getDocument().addDocumentListener(listener);
        txtPassword.getDocument().addDocumentListener(listener);

        chkShowPassword.addActionListener(e -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : defaultEchoChar));
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> handleExit());
        txtUsername.addActionListener(e -> {
            if (txtPassword.getPassword().length > 0) {
                handleLogin();
            } else {
                txtPassword.requestFocusInWindow();
            }
        });
        txtPassword.addActionListener(e -> handleLogin());

        getRootPane().setDefaultButton(btnLogin);
        getRootPane().registerKeyboardAction(
                e -> handleExit(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                toFront();
                requestFocus();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                toFront();
                requestFocus();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void validateForm() {
        boolean hasUsername = !txtUsername.getText().trim().isEmpty();
        boolean hasPassword = txtPassword.getPassword().length > 0;

        UiStyles.setFieldValidation(txtUsername, hasUsername || txtUsername.getText().trim().isEmpty());
        UiStyles.setFieldValidation(txtPassword, hasPassword || txtPassword.getPassword().length == 0);

        btnLogin.setEnabled(hasUsername && hasPassword);

        if (!hasUsername && !hasPassword) {
            UiStyles.setStatus(lblStatus, "Enter your username and password to sign in.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        } else if (!hasUsername) {
            UiStyles.setStatus(lblStatus, "Username is required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
        } else if (!hasPassword) {
            UiStyles.setStatus(lblStatus, "Password is required.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
        } else {
            UiStyles.setStatus(lblStatus, "Credentials look good. Press Enter or click Login.", UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            UiStyles.setFieldValidation(txtUsername, false);
            UiStyles.setStatus(lblStatus, "Please enter your username.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            txtUsername.requestFocusInWindow();
            return;
        }
        if (password.isEmpty()) {
            UiStyles.setFieldValidation(txtPassword, false);
            UiStyles.setStatus(lblStatus, "Please enter your password.", UiStyles.STATUS_WARNING_BG, UiStyles.WARNING);
            txtPassword.requestFocusInWindow();
            return;
        }

        setBusy(true, "Checking account...");
        SwingUtilities.invokeLater(() -> {
            Employee employee = employeeController.authenticate(username, password);
            if (employee == null) {
                setBusy(false, "Invalid username or password.");
                UiStyles.setFieldValidation(txtPassword, false);
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
                return;
            }

            authenticatedEmployee = employee;
            setBusy(false, "Welcome " + employee.getFullName() + " (" + EmployeeRoles.toDisplayName(employee.getRole()) + ").");
            dispose();
        });
    }

    private void handleExit() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Do you want to close the application?",
                "Exit Login",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (option == JOptionPane.YES_OPTION) {
            authenticatedEmployee = null;
            dispose();
        }
    }

    private void setBusy(boolean busy, String message) {
        txtUsername.setEnabled(!busy);
        txtPassword.setEnabled(!busy);
        chkShowPassword.setEnabled(!busy);
        btnLogin.setEnabled(!busy && !txtUsername.getText().trim().isEmpty() && txtPassword.getPassword().length > 0);
        btnExit.setEnabled(!busy);

        if (busy) {
            UiStyles.setStatus(lblStatus, message, UiStyles.STATUS_INFO_BG, UiStyles.PRIMARY);
        } else if (authenticatedEmployee != null) {
            UiStyles.setStatus(lblStatus, message, UiStyles.STATUS_SUCCESS_BG, UiStyles.SUCCESS);
        } else if ("Invalid username or password.".equals(message)) {
            UiStyles.setStatus(lblStatus, message, UiStyles.STATUS_ERROR_BG, UiStyles.DANGER);
        } else {
            UiStyles.setStatus(lblStatus, message, UiStyles.STATUS_INFO_BG, UiStyles.MUTED);
        }
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(UiStyles.PRIMARY);
        imagePanel.setPreferredSize(new Dimension(300, 410));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            String imagePath = "../resources/images.jpeg";
            java.net.URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                // Scale image to fit panel
                Image scaledImage = icon.getImage().getScaledInstance(280, 390, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                // Fallback: show placeholder if image not found
                JLabel placeholderLabel = new JLabel("Hotel Image");
                placeholderLabel.setFont(new Font("Dialog", Font.BOLD, 18));
                placeholderLabel.setForeground(Color.WHITE);
                placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
                placeholderLabel.setVerticalAlignment(SwingConstants.CENTER);
                imagePanel.add(placeholderLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Show placeholder if there's an error
            JLabel errorLabel = new JLabel("Image Not Found");
            errorLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setVerticalAlignment(SwingConstants.CENTER);
            imagePanel.add(errorLabel, BorderLayout.CENTER);
        }

        return imagePanel;
    }
}
