package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the flight booking system.
 */
public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String password; // New field for password
    private final List<Booking> bookings;
    private boolean isDeleted = false;

    /**
     * Constructs a new Customer with the specified details.
     *
     * @param id       the unique identifier for the customer
     * @param name     the name of the customer
     * @param phone    the phone number of the customer
     * @param email    the email address of the customer
     * @param password the password of the customer
     */
    public Customer(int id, String name, String phone, String email, String password) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.bookings = new ArrayList<>();
    }

    // Getters

    /**
     * Gets the unique identifier of the customer.
     *
     * @return the customer ID
     */
    public int getId() { return id; }

    /**
     * Gets the name of the customer.
     *
     * @return the customer name
     */
    public String getName() { return name; }

    /**
     * Gets the phone number of the customer.
     *
     * @return the customer phone number
     */
    public String getPhone() { return phone; }

    /**
     * Gets the email address of the customer.
     *
     * @return the customer email address
     */
    public String getEmail() { return email; }

    /**
     * Gets the password of the customer.
     *
     * @return the customer password
     */
    public String getPassword() { return password; }

    /**
     * Checks if the customer is marked as deleted.
     *
     * @return true if the customer is deleted, false otherwise
     */
    public boolean isDeleted() { return isDeleted; }

    /**
     * Gets the list of bookings for the customer.
     *
     * @return a copy of the list of bookings
     */
    public List<Booking> getBookings() { return new ArrayList<>(bookings); }

    // Setters

    /**
     * Sets the name of the customer.
     *
     * @param name the new name of the customer
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the phone number of the customer.
     *
     * @param phone the new phone number of the customer
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Sets the email address of the customer.
     *
     * @param email the new email address of the customer
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets the password of the customer.
     *
     * @param password the new password of the customer
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Marks the customer as deleted or not deleted.
     *
     * @param deleted true to mark the customer as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }

    /**
     * Adds a booking to the customer's list of bookings.
     *
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) { bookings.add(booking); }

    /**
     * Cancels a booking from the customer's list of bookings.
     *
     * @param booking the booking to cancel
     */
    public void cancelBooking(Booking booking) { bookings.remove(booking); }

    /**
     * Gets a short description of the customer's details.
     *
     * @return a string containing the customer's ID, name, phone, and email
     */
    public String getDetailsShort() {
        return "Customer #" + id + " - " + name + " - " + phone + " - " + email;
    }
}