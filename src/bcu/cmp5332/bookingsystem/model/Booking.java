package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;

/**
 * This class represents a booking in the Flight Booking System.
 */
public class Booking {
    private int id;
    private final Customer customer;
    private final Flight flight;
    private final LocalDate bookingDate;
    private boolean isCancelled = false;
    private double bookingFee;

    /**
     * Constructs a new Booking with the specified details.
     *
     * @param id the booking ID
     * @param customer the customer who made the booking
     * @param flight the flight associated with the booking
     * @param bookingDate the date the booking was made
     * @param bookingFee the fee for the booking
     */
    public Booking(int id, Customer customer, Flight flight, LocalDate bookingDate, double bookingFee) {
        this.id = id;
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.bookingFee = bookingFee;
    }

    /**
     * Gets the booking ID.
     *
     * @return the booking ID
     */
    public int getId() { return id; }

    /**
     * Gets the customer who made the booking.
     *
     * @return the customer
     */
    public Customer getCustomer() { return customer; }

    /**
     * Gets the flight associated with the booking.
     *
     * @return the flight
     */
    public Flight getFlight() { return flight; }

    /**
     * Gets the date the booking was made.
     *
     * @return the booking date
     */
    public LocalDate getBookingDate() { return bookingDate; }

    /**
     * Checks if the booking is cancelled.
     *
     * @return true if the booking is cancelled, false otherwise
     */
    public boolean isCancelled() { return isCancelled; }

    /**
     * Gets the fee for the booking.
     *
     * @return the booking fee
     */
    public double getBookingFee() { return bookingFee; }

    /**
     * Cancels the booking. Removes the customer from the flight's passenger list
     * and updates the customer's booking status.
     */
    public void cancel() {
        isCancelled = true;
        flight.getPassengers().remove(customer);
        customer.cancelBooking(this);
    }

    /**
     * Returns a string representation of the booking.
     *
     * @return a string representation of the booking
     */
    @Override
    public String toString() {
        return "Booking #" + id + " for " + customer.getName() + " on flight " + flight.getFlightNumber() +
               " (" + bookingDate + "), Fee: $" + bookingFee;
    }
}