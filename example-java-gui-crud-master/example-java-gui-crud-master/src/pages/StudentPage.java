package pages;

import dal.students.StudentDAO;
import models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

public class StudentPage extends JFrame {
    private final StudentDAO studentDao = new StudentDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter; // NEW: For sorting table
    private JTextField studentNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField programField;
    private JSpinner levelSpinner;
    private JTextField searchField; // NEW: Search functionality
    private JComboBox<String> programFilterCombo; // NEW: Program filter
    private JLabel statusLabel; // NEW: Status feedback
    private JLabel recordCountLabel; // NEW: Record count display
    private JButton addButton, updateButton, deleteButton, logoutButton, clearButton, refreshButton; // NEW: Additional buttons

    public StudentPage() {

        setTitle("Student Record Management System - Student CRUD");
        setSize(1000, 700); // Made larger to accommodate new features
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Set background color
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create main panels
        createFormPanel();
        setFieldsEditable(true);
        createTablePanel();
        createStatusPanel();

        // Load initial data
        loadStudents();
        updateRecordCount();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method that creates the form panel
    private void createFormPanel() {
        JPanel mainFormPanel = new JPanel(new BorderLayout());
        mainFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Student Information"));
        mainFormPanel.setBackground(Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize form fields with enhanced styling
        studentNumberField = new JTextField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        programField = new JTextField(15);
        levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Style the fields
        styleTextField(studentNumberField);
        styleTextField(firstNameField);
        styleTextField(lastNameField);
        styleTextField(programField);
        levelSpinner.setPreferredSize(new Dimension(100, 30));

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Student Number:", studentNumberField);
        addFormRow(formPanel, gbc, row++, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, row++, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, row++, "Program:", programField);
        addFormRow(formPanel, gbc, row++, "Level:", levelSpinner);

        // Button panel with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createStyledButton("Add", new Color(40, 167, 69), Color.WHITE);
        updateButton = createStyledButton("Update", new Color(0, 123, 255), Color.WHITE);
        deleteButton = createStyledButton("Delete", new Color(220, 53, 69), Color.WHITE);
        clearButton = createStyledButton("Clear", new Color(108, 117, 125), Color.WHITE); // NEW
        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE); // NEW
        logoutButton = createStyledButton("Log Out", new Color(52, 58, 64), Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        mainFormPanel.add(formPanel, BorderLayout.CENTER);
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainFormPanel, BorderLayout.NORTH);

        // Add event listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields()); // NEW
        refreshButton.addActionListener(e -> refreshData()); // NEW
        logoutButton.addActionListener(e -> logoutStudent());
    }

    // Method that creates the table panel
    private void createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Student Records"));

        // NEW: Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);

        // Search field
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        styleTextField(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        searchPanel.add(searchField);

        // Program filter
        searchPanel.add(new JLabel("Filter by Program:"));
        programFilterCombo = new JComboBox<>();
        programFilterCombo.addItem("All Programs");
        programFilterCombo.addActionListener(e -> filterTable());
        searchPanel.add(programFilterCombo);

        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table setup with enhanced features
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Student Number", "First Name", "Last Name", "Program", "Level"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        table = new JTable(tableModel);

        // NEW: Add sorting capability
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Enhanced table styling
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(0, 123, 255, 50));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Student Number
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // First Name
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Last Name
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Program
        table.getColumnModel().getColumn(5).setPreferredWidth(60);  // Level

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Enhanced table click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    // Convert view row to model row (important for sorted tables)
                    int modelRow = table.convertRowIndexToModel(row);
                    populateFieldsFromTable(modelRow);
                    showStatus("Selected student: " +
                            tableModel.getValueAt(modelRow, 2) + " " +
                            tableModel.getValueAt(modelRow, 3), Color.BLUE);
                }
            }
        });
    }

    // Methods that creates the status panel
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Record count label
        recordCountLabel = new JLabel("Records: 0");
        recordCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        recordCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(recordCountLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    // NEW: Enhanced field styling
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setPreferredSize(new Dimension(150, 30));
    }

    // NEW: Create styled buttons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setPreferredSize(new Dimension(80, 35));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    // NEW: Status message display
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);

        // Clear status after 3 seconds
        Timer timer = new Timer(3000, e -> {
            statusLabel.setText("Ready");
            statusLabel.setForeground(Color.BLACK);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // NEW: Update record count
    private void updateRecordCount() {
        int totalRecords = tableModel.getRowCount();
        int visibleRecords = table.getRowCount();
        if (totalRecords == visibleRecords) {
            recordCountLabel.setText("Records: " + totalRecords);
        } else {
            recordCountLabel.setText("Showing: " + visibleRecords + " of " + totalRecords);
        }
    }

    // NEW: Search and filter functionality
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        String selectedProgram = (String) programFilterCombo.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Search filter
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText));
        }

        // Program filter
        if (selectedProgram != null && !selectedProgram.equals("All Programs")) {
            filters.add(RowFilter.regexFilter("^" + selectedProgram + "$", 4)); // Column 4 is Program
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }

        updateRecordCount();
    }

    // NEW: Populate program filter dropdown
    private void updateProgramFilter() {
        String currentSelection = (String) programFilterCombo.getSelectedItem();
        programFilterCombo.removeAllItems();
        programFilterCombo.addItem("All Programs");

        // Get unique programs from the data
        java.util.Set<String> programs = new java.util.HashSet<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String program = tableModel.getValueAt(i, 4).toString();
            programs.add(program);
        }

        for (String program : programs) {
            programFilterCombo.addItem(program);
        }

        // Restore selection if possible
        if (currentSelection != null) {
            programFilterCombo.setSelectedItem(currentSelection);
        }
    }

    // NEW: Refresh data method
    private void refreshData() {
        showStatus("Refreshing data...", Color.BLUE);
        loadStudents();
        updateRecordCount();
        showStatus("Data refreshed successfully!", Color.GREEN);
    }

    // NEW: Populate fields from selected table row
    private void populateFieldsFromTable(int modelRow) {
        studentNumberField.setText(tableModel.getValueAt(modelRow, 1).toString());
        firstNameField.setText(tableModel.getValueAt(modelRow, 2).toString());
        lastNameField.setText(tableModel.getValueAt(modelRow, 3).toString());
        programField.setText(tableModel.getValueAt(modelRow, 4).toString());
        levelSpinner.setValue((int) tableModel.getValueAt(modelRow, 5));

        // Disable name/number fields for editing (only allow program/level changes)
        setFieldsEditable(false);
    }

    // Method to add new rows
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    // Method to load data from database
    private void loadStudents() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentDao.getAllStudents();
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                        student.getId(),
                        student.getStudentNumber(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getProgram(),
                        student.getLevel()
                });
            }
            updateProgramFilter(); // Update the program filter dropdown
            showStatus("Loaded " + students.size() + " student records.", Color.GREEN);
        } catch (Exception e) {
            showStatus("Error loading students: " + e.getMessage(), Color.RED);
            JOptionPane.showMessageDialog(this, "Error loading student data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add new set of data
    private void addStudent() {
        if (!validateFields()) {
            return;
        }

        try {
            String studentNumber = studentNumberField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String program = programField.getText().trim();
            int level = (int) levelSpinner.getValue();

            Student newStudent = new Student(0, studentNumber, firstName, lastName, program, level);
            studentDao.addStudent(newStudent);

            loadStudents();
            clearFields();
            updateRecordCount();
            showStatus("Student added successfully: " + firstName + " " + lastName, Color.GREEN);

        } catch (Exception e) {
            showStatus("Error adding student: " + e.getMessage(), Color.RED);
            JOptionPane.showMessageDialog(this, "Error adding student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to update existing data
    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStatus("Please select a student to update.", Color.RED);
            JOptionPane.showMessageDialog(this, "Please select a student from the table to update.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) tableModel.getValueAt(modelRow, 0);

            // Original values from table model
            String originalStudentNumber = (String) tableModel.getValueAt(modelRow, 1);
            String originalFirstName = (String) tableModel.getValueAt(modelRow, 2);
            String originalLastName = (String) tableModel.getValueAt(modelRow, 3);

            // Current values from input fields
            String studentNumber = studentNumberField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String program = programField.getText().trim();
            int level = (int) levelSpinner.getValue();

            // Check for disallowed changes
            if (!studentNumber.equals(originalStudentNumber) ||
                    !firstName.equalsIgnoreCase(originalFirstName) ||
                    !lastName.equalsIgnoreCase(originalLastName)) {
                showStatus("You can only update the program and grade level.", Color.RED);
                JOptionPane.showMessageDialog(this,
                        "You are only allowed to update the course and grade level.\nStudent number and name cannot be changed.",
                        "Update Not Allowed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Proceed with update
            Student updatedStudent = new Student(id, studentNumber, firstName, lastName, program, level);
            studentDao.updateStudent(updatedStudent);

            loadStudents();
            clearFields();
            updateRecordCount();
            showStatus("Student updated successfully: " + firstName + " " + lastName, Color.GREEN);

        } catch (Exception e) {
            showStatus("Error updating student: " + e.getMessage(), Color.RED);
            JOptionPane.showMessageDialog(this, "Error updating student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to delete data
    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStatus("Please select a student to delete.", Color.RED);
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.");
            return;
        }

        try {
            int modelRow = table.convertRowIndexToModel(row);
            String studentName = tableModel.getValueAt(modelRow, 2) + " " + tableModel.getValueAt(modelRow, 3);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete student: " + studentName + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(modelRow, 0);
                studentDao.deleteStudent(id);
                loadStudents();
                clearFields();
                updateRecordCount();
                showStatus("Student deleted: " + studentName, Color.RED);
            }
        } catch (Exception e) {
            showStatus("Error deleting student: " + e.getMessage(), Color.RED);
            JOptionPane.showMessageDialog(this, "Error deleting student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to clear existing inputs from text fields
    private void clearFields() {
        studentNumberField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        programField.setText("");
        levelSpinner.setValue(1);
        table.clearSelection();

        // Enable all fields for new entry
        setFieldsEditable(true);
        showStatus("Fields cleared - Ready for new entry.", Color.GRAY);
    }

    // NEW: Enhanced field validation
    private boolean validateFields() {
        String studentNumber = studentNumberField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String program = programField.getText().trim();

        if (studentNumber.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || program.isEmpty()) {
            showStatus("Please fill in all required fields.", Color.RED);
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Additional validation
        if (studentNumber.length() < 5) {
            showStatus("Student number must be at least 5 characters.", Color.RED);
            JOptionPane.showMessageDialog(this, "Student number must be at least 5 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    // NEW: Log out button that redirects to log in page
    private void logoutStudent() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?\nAny unsaved changes will be lost.",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            showStatus("Logging out...", Color.BLUE);
            new LoginPage();
            dispose();
        }
    }

    // Add this method to control field editability
    private void setFieldsEditable(boolean editable) {
        studentNumberField.setEditable(editable);
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        // Program and level are always editable
    }


}