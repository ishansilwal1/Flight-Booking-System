package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that displays the details of a specific customer
 * in the flight booking system. This command retrieves and shows the
 * shortened details of a customer based on their ID.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see Customer
 */
public class ShowCustomer implements Command {
    
    /** The ID of the customer to be displayed */
    private final int customerId;
    
    /**
     * Constructs a new ShowCustomer command for the specified customer ID.
     * 
     * @param customerId The unique identifier of the customer to be displayed
     */
    public ShowCustomer(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Executes the ShowCustomer command.
     * Retrieves the customer with the specified ID from the flight booking system
     * and displays their shortened details to the console using System.out.println().
     *
     * @param fbs The FlightBookingSystem instance containing the customer data
     * @throws FlightBookingSystemException if no customer exists with the specified ID
     * @see Customer#getDetailsShort()
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer customer = fbs.getCustomerByID(customerId);
        System.out.println(customer.getDetailsShort());
    }
}