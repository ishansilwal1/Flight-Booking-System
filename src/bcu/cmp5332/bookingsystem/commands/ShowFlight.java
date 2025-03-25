package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that displays the detailed information of a specific flight
 * in the flight booking system. This command retrieves and shows the
 * complete details of a flight based on its ID.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see Flight
 */
public class ShowFlight implements Command {
    
    /** The ID of the flight to be displayed */
    private final int flightId;
    
    /**
     * Constructs a new ShowFlight command for the specified flight ID.
     * 
     * @param flightId The unique identifier of the flight to be displayed
     */
    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }
    
    /**
     * Executes the ShowFlight command.
     * Retrieves the flight with the specified ID from the flight booking system
     * and displays its detailed information to the console using System.out.println().
     * Uses getDetailsLong() to show comprehensive flight information.
     *
     * @param fbs The FlightBookingSystem instance containing the flight data
     * @throws FlightBookingSystemException if no flight exists with the specified ID
     * @see Flight#getDetailsLong()
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Flight flight = fbs.getFlightByID(flightId);
        System.out.println(flight.getDetailsLong());
    }
}