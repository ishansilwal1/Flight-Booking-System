package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The {@code Help} class represents a command to display the help message containing a list of available commands.
 * This command provides users with guidance on how to use the system.
 */
public class Help implements Command {

    /**
     * Executes the command to display the help message containing a list of available commands.
     *
     * @param flightBookingSystem the flight booking system
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) {
        System.out.println(Command.HELP_MESSAGE);
    }
}