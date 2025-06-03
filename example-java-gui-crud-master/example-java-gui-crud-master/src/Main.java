// Sumiran, John Rycel V.
// BSIT 2-2
// Object-Oriented Programming
// GUi-JDBC Workshop

import pages.LoginPage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}