package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class represents a window that displays a list of customers in the flight booking system.
 */
public class CustomerListWindow extends JFrame {

    /**
     * Constructs a new CustomerListWindow.
     *
     * @param fbs the flight booking system from which the customer data will be retrieved
     */
    public CustomerListWindow(FlightBookingSystem fbs) {
        setTitle("Customer List");
        List<Customer> customers = fbs.getCustomers();
        String[] columns = {"ID", "Name", "Phone", "Active"};
        Object[][] data = new Object[customers.size()][4];
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.isDeleted() ? "No" : "Yes";
        }
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}