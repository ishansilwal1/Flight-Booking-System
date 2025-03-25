/**
 * The {@code AddCustomer} class represents a command to add a new customer
 * to the flight booking system. It implements the {@code Command} interface.
 */
package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to add a new customer to the flight booking system.
 */
public class AddCustomer implements Command {
    
    /** The name of the customer. */
    private final String name;
    
    /** The phone number of the customer. */
    private final String phone;
    
    /** The email address of the customer. */
    private final String email;
    
    /** The password of the customer. */
    private final String password;
    
    /**
     * Constructs an {@code AddCustomer} command with the specified name,
     * phone number, email, and password.
     *
     * @param name the name of the customer
     * @param phone the phone number of the customer
     * @param email the email address of the customer
     * @param password the password of the customer
     */
    public AddCustomer(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }
    
    /**
     * Executes the command by adding a new customer to the flight booking system.
     *
     * @param flightBookingSystem the flight booking system where the customer will be added
     * @throws FlightBookingSystemException if an error occurs while adding the customer
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        int maxId = flightBookingSystem.getAllCustomers().stream().mapToInt(Customer::getId).max().orElse(0);
        Customer customer = new Customer(maxId + 1, name, phone, email, password);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added.");
    }
}