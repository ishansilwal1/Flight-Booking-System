/**
 * The {@code CancelBooking} class represents a command to cancel a booking
 * in the flight booking system. It implements the {@code Command} interface.
 */
package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to cancel an existing booking in the flight booking system.
 */
public class CancelBooking implements Command {
    
    /** The ID of the booking to be cancelled. */
    private final int bookingId;
    
    /** The cancellation fee to be charged. */
    private final double cancellationFee;
    
    /**
     * Constructs a {@code CancelBooking} command with the specified booking ID and cancellation fee.
     *
     * @param bookingId the ID of the booking to be cancelled
     * @param cancellationFee the cancellation fee to be applied
     */
    public CancelBooking(int bookingId, double cancellationFee) {
        this.bookingId = bookingId;
        this.cancellationFee = cancellationFee;
    }
    
    /**
     * Executes the command by cancelling the specified booking in the flight booking system.
     *
     * @param fbs the flight booking system where the booking will be cancelled
     * @throws FlightBookingSystemException if an error occurs while cancelling the booking
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        fbs.cancelBooking(bookingId, cancellationFee);
        System.out.println("Booking " + bookingId + " cancelled with cancellation fee: $" + cancellationFee);
    }
}