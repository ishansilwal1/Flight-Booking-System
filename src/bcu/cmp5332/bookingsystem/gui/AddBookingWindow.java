package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

/**
 * This class represents a window for adding a new booking in the flight booking system.
 */
public class AddBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField custIdField = new JTextField(10);
    private JTextField flightIdField = new JTextField(10);
    private JLabel feeLabel = new JLabel("Booking Fee: $0.00");
    private JButton computeFeeButton = new JButton("Compute Fee");
    private JButton bookButton = new JButton("Book Flight");
    
    private double computedFee = 0.0;
    
    /**
     * Constructs a new AddBookingWindow.
     *
     * @param mw the main window of the application
     */
    public AddBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }
    
    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Create New Booking");
        setSize(350, 250);
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Customer ID:"));
        panel.add(custIdField);
        panel.add(new JLabel("Flight ID:"));
        panel.add(flightIdField);
        panel.add(new JLabel("")); 
        panel.add(computeFeeButton);
        panel.add(new JLabel("Booking Fee:"));
        panel.add(feeLabel);
        panel.add(new JLabel("")); 
        panel.add(bookButton);
        
        // If a customer is logged in, preselect their ID and disable editing.
        if (mw.getLoggedInCustomerId() != null) {
            custIdField.setText(String.valueOf(mw.getLoggedInCustomerId()));
            custIdField.setEditable(false);
        }
        
        computeFeeButton.addActionListener(this);
        bookButton.addActionListener(this);
        
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
        if(e.getSource() == computeFeeButton) {
            computeFee();
        } else if(e.getSource() == bookButton) {
            createBooking();
        }
    }
    
    /**
     * Computes the booking fee for the specified flight.
     */
    private void computeFee() {
        try {
            int flightId = Integer.parseInt(flightIdField.getText());
            double fee = mw.getFlightBookingSystem().getFlightByID(flightId).calculatePrice(LocalDate.now());
            computedFee = fee;
            feeLabel.setText(String.format("Booking Fee: $%.2f", fee));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Flight ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates a new booking with the specified customer ID and flight ID.
     */
    private void createBooking() {
        try {
            int custId = Integer.parseInt(custIdField.getText());
            int flightId = Integer.parseInt(flightIdField.getText());
            AddBooking addCmd = new AddBooking(custId, flightId, LocalDate.now());
            addCmd.execute(mw.getFlightBookingSystem());
            Booking latestBooking = mw.getFlightBookingSystem().getBookings().get(mw.getFlightBookingSystem().getBookings().size() - 1);
            JOptionPane.showMessageDialog(this, "Booking created successfully.\nFee: $" + String.format("%.2f", computedFee));
            mw.displayBookings();
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}