package bcu.cmp5332.bookingsystem.main;

/**
 * This class represents an exception specific to the Flight Booking System.
 */
public class FlightBookingSystemException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new FlightBookingSystemException with the specified detail message.
     *
     * @param message the detail message
     */
    public FlightBookingSystemException(String message) {
        super(message);
    }
}