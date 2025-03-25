package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The {@code DeleteCustomer} class represents a command to delete a customer from the flight booking system.
 * This command removes a customer identified by their unique ID.
 *
 */
public class DeleteCustomer implements Command {

    private final int customerId; // ID of the customer to be deleted

    /**
     * Constructs a {@code DeleteCustomer} object with the specified customer ID.
     *
     * @param customerId the ID of the customer to be deleted
     */
    public DeleteCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the command to delete a customer from the flight booking system.
     *
     * @param fbs the flight booking system from which the customer will be deleted
     * @throws FlightBookingSystemException if the customer is not found or an error occurs during deletion
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        System.out.println("Executing DeleteCustomer command for customer ID: " + customerId);
        
        Customer customer = fbs.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer not found for ID: " + customerId);
        }
        
        fbs.deleteCustomer(customerId);
        System.out.println("Customer #" + customerId + " deleted.");
    }
}