package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The Command interface represents an executable command in the flight booking system.
 * Each command must implement the {@code execute} method, which performs a specific action
 * on the {@link FlightBookingSystem}.
 */
public interface Command {
    
    /**
     * Help message displaying available commands and their usage.
     */
    public static final String HELP_MESSAGE = "Commands:\n"
            + "\tlistflights                               print all flights\n"
            + "\tlistcustomers                             print all customers\n"
            + "\taddflight                                 add a new flight\n"
            + "\taddcustomer                               add a new customer\n"
            + "\tshowflight [flight id]                    show flight details\n"
            + "\tshowcustomer [customer id]                show customer details\n"
            + "\taddbooking [customer id] [flight id]      add a new booking\n"
            + "\tcancelbooking [customer id] [flight id]   cancel a booking\n"
            + "\tupdatebooking [booking id] [flight id]    update a booking\n"
            + "\tdeleteflight [flight id]                  delete a flight\n"
            + "\tdeletecustomer [customer id]              delete a customer\n"
            + "\tupdatecustomer [customer id] [name] [phone] [email] [password] update a customer\n"
            + "\tloadgui                                   loads the GUI version of the app\n"
            + "\thelp                                      prints this help message\n"
            + "\texit                                      exits the program";

    /**
     * Executes the command using the given {@link FlightBookingSystem} instance.
     *
     * @param flightBookingSystem the flight booking system on which the command is executed
     * @throws FlightBookingSystemException if an error occurs during command execution
     */
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException;
}