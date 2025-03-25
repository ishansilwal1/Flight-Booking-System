package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that updates an existing booking to a new flight.
 * This command handles the process of canceling the existing booking and
 * creating a new booking with the same customer on a different flight.
 * A cancellation fee of 15% of the original booking fee is applied.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see bcu.cmp5332.bookingsystem.model.Booking
 */
public class UpdateBooking implements Command {
    
    /** The ID of the booking to be updated */
    private final int bookingId;
    
    /** The ID of the new flight for the booking */
    private final int newFlightId;
    
    /**
     * Constructs a new UpdateBooking command with the specified booking and flight IDs.
     * 
     * @param bookingId The unique identifier of the booking to be updated
     * @param newFlightId The unique identifier of the new flight for the booking
     */
    public UpdateBooking(int bookingId, int newFlightId) {
        this.bookingId = bookingId;
        this.newFlightId = newFlightId;
    }
    
    /**
     * Executes the UpdateBooking command.
     * The process involves:
     * 1. Retrieving the existing booking
     * 2. Calculating the cancellation fee (15% of original booking fee)
     * 3. Canceling the original booking
     * 4. Creating a new booking with the same customer on the new flight
     * 5. Displaying a confirmation message with booking details and cancellation fee
     *
     * @param fbs The FlightBookingSystem instance managing the bookings
     * @throws FlightBookingSystemException if the original booking or new flight cannot be found,
     *         or if there are issues with canceling or creating the booking
     * @see FlightBookingSystem#getBookingByID(int)
     * @see FlightBookingSystem#cancelBooking(int, double)
     * @see FlightBookingSystem#addBooking(int, int, java.time.LocalDate)
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        var oldBooking = fbs.getBookingByID(bookingId);
        int customerId = oldBooking.getCustomer().getId();
        double cancellationFee = 0.15 * oldBooking.getBookingFee();
        fbs.cancelBooking(bookingId, cancellationFee);
        fbs.addBooking(customerId, newFlightId, oldBooking.getBookingDate());
        System.out.println("Booking updated: " + bookingId + " updated to new flight: " + newFlightId + 
                          " with cancellation fee: $" + cancellationFee);
    }
}