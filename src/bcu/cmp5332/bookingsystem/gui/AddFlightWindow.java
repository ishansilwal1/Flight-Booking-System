package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

/**
 * This class represents a window for adding a new flight in the flight booking system.
 */
public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField basePriceText = new JTextField(); // For base price
    private JTextField capacityText = new JTextField();  // For capacity

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    /**
     * Constructs a new AddFlightWindow.
     *
     * @param mw the main window of the application
     */
    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { }

        setTitle("Add a New Flight");
        setSize(400, 300);
        JPanel topPanel = new JPanel(new GridLayout(7, 2));
        topPanel.add(new JLabel("Flight No:"));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin:"));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination:"));
        topPanel.add(destinationText);
        topPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        topPanel.add(depDateText);
        topPanel.add(new JLabel("Base Price ($):"));
        topPanel.add(basePriceText);
        topPanel.add(new JLabel("Capacity:"));
        topPanel.add(capacityText);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        getContentPane().add(topPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    /**
     * Handles action events for the buttons in the window.
     *
     * @param ae the action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    /**
     * Adds a new flight to the flight booking system.
     */
    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();
            LocalDate departureDate = LocalDate.parse(depDateText.getText());
            double basePrice = Double.parseDouble(basePriceText.getText());
            int capacity = Integer.parseInt(capacityText.getText());
            
            AddFlight addFlightCmd = new AddFlight(flightNumber, origin, destination, departureDate, basePrice, capacity);
            addFlightCmd.execute(mw.getFlightBookingSystem());
            mw.displayUpcomingFlights();
            this.dispose();
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Price and Capacity must be numeric", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}