import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestGUI {

    public static void main(String[] args) {
        // Create the frame (window)
        JFrame frame = new JFrame("Test GUI");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a button
        JButton button = new JButton("Click Me!");

        // Add action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Button was clicked!");
            }
        });

        // Add the button to the frame
        frame.getContentPane().add(button);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }
}

