package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class represents a window for canceling an existing booking in the flight booking system.
 */
public class CancelBookingWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private MainWindow mw;
    private JTextField bookingIdField = new JTextField(10);
    private JButton cancelBtn = new JButton("Cancel Booking");

    /**
     * Constructs a new CancelBookingWindow.
     *
     * @param mw the main window of the application
     */
    public CancelBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Cancel Booking");
        setSize(350, 150);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Booking ID:"));
        panel.add(bookingIdField);
        panel.add(new JLabel(""));
        panel.add(cancelBtn);
        cancelBtn.addActionListener(this);
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
            int bookingId = Integer.parseInt(bookingIdField.getText());
            Booking booking = mw.getFlightBookingSystem().getBookingByID(bookingId);
            double cancellationFee = 0.15 * booking.getBookingFee();
            int confirm = JOptionPane.showConfirmDialog(this,
                "A cancellation fee of $" + String.format("%.2f", cancellationFee) + " will be applied. Confirm cancellation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bcu.cmp5332.bookingsystem.commands.CancelBooking cancelCmd = new bcu.cmp5332.bookingsystem.commands.CancelBooking(bookingId, cancellationFee);
                cancelCmd.execute(mw.getFlightBookingSystem());
                mw.displayBookings();
                this.dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for booking ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}