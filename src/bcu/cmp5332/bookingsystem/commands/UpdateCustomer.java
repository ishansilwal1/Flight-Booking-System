package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that updates the personal information of an existing customer
 * in the flight booking system. This command allows modification of a customer's
 * name, phone number, email address, and password in a single operation.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see Customer
 */
public class UpdateCustomer implements Command {
    
    /** The unique identifier of the customer whose details are to be updated */
    private final int customerId;
    
    /** The new or updated name for the customer */
    private final String name;
    
    /** The new or updated phone number for the customer */
    private final String phone;
    
    /** The new or updated email address for the customer */
    private final String email;
    
    /** The new or updated password for the customer's account */
    private final String password;
    
    /**
     * Constructs a new UpdateCustomer command with the specified customer details.
     * All parameters represent the new values to be set for the customer's information.
     * 
     * @param customerId The unique identifier of the customer to be updated
     * @param name The new name to set for the customer
     * @param phone The new phone number to set for the customer
     * @param email The new email address to set for the customer
     * @param password The new password to set for the customer's account
     */
    public UpdateCustomer(int customerId, String name, String phone, String email, String password) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }
    
    /**
     * Executes the UpdateCustomer command.
     * This method performs the following operations:
     * 1. Retrieves the customer using their ID
     * 2. Updates all their personal information with the new values
     * 3. Displays a confirmation message upon successful update
     *
     * @param flightBookingSystem The FlightBookingSystem instance containing the customer data
     * @throws FlightBookingSystemException if no customer exists with the specified ID
     * @see Customer#setName(String)
     * @see Customer#setPhone(String)
     * @see Customer#setEmail(String)
     * @see Customer#setPassword(String)
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setPassword(password);
        System.out.println("Customer #" + customerId + " updated.");
    }
}