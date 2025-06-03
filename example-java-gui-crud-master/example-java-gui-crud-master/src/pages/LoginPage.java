package pages;

import dal.admins.AdminDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginPage extends JFrame {
    private final AdminDAO adminDao = new AdminDAO();
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton logInButton;
    private final JButton signInButton;
    private final JButton clearButton; // NEW: Clear button
    private final JCheckBox showPasswordCheckBox; // NEW: Show password checkbox
    private final JLabel statusLabel; // NEW: Status label for feedback
    private final JLabel dateTimeLabel; // NEW: Current date/time display
    private int loginAttempts = 0; // NEW: Track login attempts
    private final int MAX_ATTEMPTS = 3; // NEW: Maximum login attempts

    public LoginPage() {
        setTitle("Admin Login - Student Record Management System");
        setSize(500, 400); // Made window larger to accommodate new components
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set a background color for better appearance
        getContentPane().setBackground(new Color(245, 245, 245));

        // Add some padding around components
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Label with enhanced styling
        JLabel titleLabel = new JLabel("Student Record Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(titleLabel, gbc);

        // NEW: Date and Time Label
        dateTimeLabel = new JLabel();
        updateDateTime();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateTimeLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 10, 15, 10);
        add(dateTimeLabel, gbc);

        // Reset insets and gridwidth for other components
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        add(usernameLabel, gbc);

        // Username Field with enhanced styling
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(passwordLabel, gbc);

        // Password Field with enhanced styling
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        // NEW: Add Enter key listener for quick login
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {}
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(passwordField, gbc);

        // NEW: Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(this::togglePasswordVisibility);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 10, 10, 10);
        add(showPasswordCheckBox, gbc);

        // Button Panel with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        logInButton = new JButton("Log In");
        logInButton.setPreferredSize(new Dimension(100, 35));
        logInButton.setBackground(new Color(0, 123, 255));
        logInButton.setForeground(Color.WHITE);
        logInButton.setFont(new Font("Arial", Font.BOLD, 12));
        logInButton.setFocusPainted(false);
        buttonPanel.add(logInButton);

        signInButton = new JButton("Sign Up");
        signInButton.setPreferredSize(new Dimension(100, 35));
        signInButton.setBackground(new Color(40, 167, 69));
        signInButton.setForeground(Color.WHITE);
        signInButton.setFont(new Font("Arial", Font.BOLD, 12));
        signInButton.setFocusPainted(false);
        buttonPanel.add(signInButton);

        // NEW: Clear Button
        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.setBackground(new Color(108, 117, 125));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setFocusPainted(false);
        buttonPanel.add(clearButton);

        // Add button panel to main layout
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);

        // NEW: Status Label for feedback
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(statusLabel, gbc);

        // Event listeners
        logInButton.addActionListener(e -> handleLogin());
        signInButton.addActionListener(e -> handleSignup());
        clearButton.addActionListener(e -> clearFields()); // NEW: Clear button action

        // NEW: Timer to update date/time every minute
        Timer timer = new Timer(60000, e -> updateDateTime());
        timer.start();

        setLocationRelativeTo(null);
        setVisible(true);

        // Set focus to username field on startup
        usernameField.requestFocus();
    }

    // NEW: Method to update date and time
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm");
        dateTimeLabel.setText(now.format(formatter));
    }

    // NEW: Method to toggle password visibility
    private void togglePasswordVisibility(ActionEvent e) {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('*'); // Hide password
        }
    }

    // NEW: Method to clear all fields
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        showPasswordCheckBox.setSelected(false);
        passwordField.setEchoChar('*');
        statusLabel.setText(" ");
        statusLabel.setForeground(Color.BLACK);
        usernameField.requestFocus();
    }

    // NEW: Enhanced method to show status messages
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Method that handles the user log ins
    private void handleLogin() {
        String username = usernameField.getText().trim(); // Trim whitespace
        String password = new String(passwordField.getPassword());

        // Enhanced validation
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.", Color.RED);
            return;
        }

        // NEW: Check for maximum login attempts
        if (loginAttempts >= MAX_ATTEMPTS) {
            showStatus("Maximum login attempts exceeded. Please restart the application.", Color.RED);
            logInButton.setEnabled(false);
            return;
        }

        showStatus("Authenticating...", Color.BLUE);

        try {
            boolean valid = adminDao.checkIfAdminExists(username, password);
            if (valid) {
                showStatus("Login successful! Redirecting...", Color.GREEN);
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + username + "!\nLogin successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new StudentPage();
                dispose();
            } else {
                loginAttempts++;
                showStatus("Invalid credentials. Attempt " + loginAttempts + "/" + MAX_ATTEMPTS, Color.RED);
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.\nAttempt " + loginAttempts + " of " + MAX_ATTEMPTS,
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

                // Clear password field after failed attempt
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (Exception e) {
            showStatus("Login error occurred.", Color.RED);
            JOptionPane.showMessageDialog(this,
                    "An error occurred during login. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW: Handles signups for new accounts
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Enhanced validation
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.", Color.RED);
            return;
        }

        // NEW: Password strength validation
        if (password.length() < 4) {
            showStatus("Password must be at least 4 characters long.", Color.RED);
            return;
        }

        showStatus("Creating account...", Color.BLUE);

        try {
            boolean userExists = adminDao.checkIfAdminExists(username, password);
            if (userExists) {
                showStatus("Username already exists.", Color.RED);
                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose a different username.",
                        "Registration Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Proceed with account creation
            adminDao.addSignupAccount(username, password);
            showStatus("Account created successfully!", Color.GREEN);
            int choice = JOptionPane.showConfirmDialog(this,
                    "Account created successfully for: " + username + "\nWould you like to login now?",
                    "Registration Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                new StudentPage();
                dispose();
            } else {
                clearFields();
            }
        } catch (Exception e) {
            showStatus("Failed to create account.", Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Failed to create account. Please try again.\nError: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}