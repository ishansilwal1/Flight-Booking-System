package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class represents a window for updating an existing booking in the flight booking system.
 */
public class UpdateBookingWindow extends JFrame implements ActionListener {
    private MainWindow mw;
    private JTextField bookingIdField = new JTextField(10);
    private JTextField newFlightIdField = new JTextField(10);

    /**
     * Constructs a new UpdateBookingWindow.
     *
     * @param mw the main window of the application
     */
    public UpdateBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Update Booking");
        setSize(300, 150);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Booking ID:"));
        panel.add(bookingIdField);
        panel.add(new JLabel("New Flight ID:"));
        panel.add(newFlightIdField);
        JButton updateBtn = new JButton("Update Booking");
        updateBtn.addActionListener(this);
        panel.add(new JLabel(""));
        panel.add(updateBtn);
        getContentPane().add(panel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    /**
     * Handles action events for the update button.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int bookingId = Integer.parseInt(bookingIdField.getText());
            int newFlightId = Integer.parseInt(newFlightIdField.getText());
            bcu.cmp5332.bookingsystem.commands.UpdateBooking updateCmd = new bcu.cmp5332.bookingsystem.commands.UpdateBooking(bookingId, newFlightId);
            updateCmd.execute(mw.getFlightBookingSystem());
            JOptionPane.showMessageDialog(this, "Booking updated successfully.");
            mw.displayBookings();
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}