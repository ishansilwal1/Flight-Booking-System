/**
 * The {@code AddBooking} class represents a command to add a new booking
 * in the flight booking system. It implements the {@code Command} interface.
 */
package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

/**
 * Command to add a new booking to the flight booking system.
 */
public class AddBooking implements Command {
    
    /** The ID of the customer making the booking. */
    private final int customerId;
    
    /** The ID of the flight being booked. */
    private final int flightId;
    
    /** The date of the booking. */
    private final LocalDate bookingDate;

    /**
     * Constructs an {@code AddBooking} command with the specified customer ID,
     * flight ID, and booking date.
     *
     * @param customerId the ID of the customer
     * @param flightId the ID of the flight
     * @param bookingDate the date of the booking
     */
    public AddBooking(int customerId, int flightId, LocalDate bookingDate) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
    }

    /**
     * Executes the command by adding a booking to the flight booking system.
     *
     * @param flightBookingSystem the flight booking system where the booking will be added
     * @throws FlightBookingSystemException if an error occurs while adding the booking
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Booking booking = flightBookingSystem.addBooking(customerId, flightId, bookingDate);
        System.out.println("Booking added: " + booking);
    }
}