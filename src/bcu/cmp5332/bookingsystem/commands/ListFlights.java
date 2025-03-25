package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command implementation that lists all flights in the flight booking system.
 * This command displays a shortened version of flight details for each flight
 * currently registered in the system.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see bcu.cmp5332.bookingsystem.model.Flight
 */
public class ListFlights implements Command {
    
    /**
     * Executes the ListFlights command.
     * Retrieves all flights from the flight booking system and prints their
     * shortened details to the console using System.out.println().
     * The details are obtained using the getDetailsShort() method of each flight.
     *
     * @param fbs The FlightBookingSystem instance containing the flight data
     * @throws IllegalStateException if the flight booking system is null
     * @see bcu.cmp5332.bookingsystem.model.Flight#getDetailsShort()
     */
    @Override
    public void execute(FlightBookingSystem fbs) {
        fbs.getFlights().forEach(flight -> System.out.println(flight.getDetailsShort()));
    }
}