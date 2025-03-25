package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The {@code DeleteFlight} class represents a command to delete a flight from the flight booking system.
 * This command removes a flight identified by its unique ID.
 *
 */
public class DeleteFlight implements Command {

    private final int flightId; // ID of the flight to be deleted

    /**
     * Constructs a {@code DeleteFlight} object with the specified flight ID.
     *
     * @param flightId the ID of the flight to be deleted
     */
    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the command to delete a flight from the flight booking system.
     *
     * @param fbs the flight booking system from which the flight will be deleted
     * @throws FlightBookingSystemException if the flight is not found or an error occurs during deletion
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        System.out.println("Executing DeleteFlight command for flight ID: " + flightId);
        
        Flight flight = fbs.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight not found for ID: " + flightId);
        }
        
        fbs.deleteFlight(flightId);
        System.out.println("Flight #" + flightId + " deleted.");
    }
}