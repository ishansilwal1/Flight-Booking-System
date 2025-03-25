package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class represents a window for adding a new customer in the flight booking system.
 */
public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField nameField = new JTextField(20);
    private JTextField phoneField = new JTextField(15);
    private JTextField emailField = new JTextField(30);
    private JPasswordField passwordField = new JPasswordField(20);

    /**
     * Constructs a new AddCustomerWindow.
     *
     * @param mw the main window of the application
     */
    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Add New Customer");
        setSize(400, 250);
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        panel.add(new JLabel(""));
        panel.add(addBtn);
        getContentPane().add(panel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    /**
     * Handles action events for the buttons in the window.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            AddCustomer addCmd = new AddCustomer(name, phone, email, password);
            addCmd.execute(mw.getFlightBookingSystem());
            mw.displayAllCustomers();
            this.dispose();
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}