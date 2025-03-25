package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that lists all customers in the flight booking system.
 * This command displays a shortened version of customer details for each customer
 * currently registered in the system.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see bcu.cmp5332.bookingsystem.model.Customer
 */
public class ListCustomers implements Command {
    
    /**
     * Executes the ListCustomers command.
     * Retrieves all customers from the flight booking system and prints their
     * shortened details to the console using System.out.println().
     * The details are obtained using the getDetailsShort() method of each customer.
     *
     * @param fbs The FlightBookingSystem instance containing the customer data
     * @throws IllegalStateException if the flight booking system is null
     * @see bcu.cmp5332.bookingsystem.model.Customer#getDetailsShort()
     */
    @Override
    public void execute(FlightBookingSystem fbs) {
        fbs.getCustomers().forEach(customer -> System.out.println(customer.getDetailsShort()));
    }
}