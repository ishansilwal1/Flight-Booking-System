package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * This class is responsible for parsing commands from the user input.
 */
public class CommandParser {

    /**
     * Parses a command line and returns the corresponding Command object.
     *
     * @param line the command line input
     * @return the Command object corresponding to the input
     * @throws IOException if an I/O error occurs
     * @throws FlightBookingSystemException if the command is invalid or an error occurs during command execution
     */
    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.split(" ", 3);
            String cmd = parts[0];

            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Flight Number: ");
                String flightNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();
                LocalDate departureDate = parseDateWithAttempts(reader);
                System.out.print("Base Price: ");
                double basePrice = Double.parseDouble(reader.readLine());
                System.out.print("Capacity: ");
                int capacity = Integer.parseInt(reader.readLine());
                return new AddFlight(flightNumber, origin, destination, departureDate, basePrice, capacity);
            } else if (cmd.equals("addcustomer")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Customer Name: ");
                String name = reader.readLine();
                System.out.print("Phone: ");
                String phone = reader.readLine();
                System.out.print("Email: ");
                String email = reader.readLine();
                System.out.print("Password: ");
                String password = reader.readLine();
                return new AddCustomer(name, phone, email, password);
            } else if (cmd.equals("listflights")) {
                return new ListFlights();
            } else if (cmd.equals("listcustomers")) {
                return new ListCustomers();
            } else if (cmd.equals("showflight") && parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                return new ShowFlight(id);
            } else if (cmd.equals("showcustomer") && parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                return new ShowCustomer(id);
            } else if (cmd.equals("addbooking") && parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                return new AddBooking(customerId, flightId, LocalDate.now());
            } else if (cmd.equals("updatebooking") && parts.length == 3) {
                int bookingId = Integer.parseInt(parts[1]);
                int newFlightId = Integer.parseInt(parts[2]);
                return new UpdateBooking(bookingId, newFlightId);
            } else if (cmd.equals("cancelbooking") && parts.length == 2) {
                int bookingId = Integer.parseInt(parts[1]);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Cancellation Fee: ");
                double cancellationFee = Double.parseDouble(reader.readLine());
                return new CancelBooking(bookingId, cancellationFee);
            } else if (cmd.equals("deleteflight") && parts.length == 2) {
                int flightId = Integer.parseInt(parts[1]);
                return new DeleteFlight(flightId);
            } else if (cmd.equals("deletecustomer") && parts.length == 2) {
                int customerId = Integer.parseInt(parts[1]);
                return new DeleteCustomer(customerId);
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (cmd.equals("help")) {
                return new Help();
            }
        } catch (NumberFormatException ex) {
            // fall through to error
        }
        throw new FlightBookingSystemException("Invalid command.");
    }

    /**
     * Parses a date from the user input with a specified number of attempts.
     *
     * @param br the BufferedReader to read user input
     * @param attempts the number of attempts allowed
     * @return the parsed LocalDate
     * @throws IOException if an I/O error occurs
     * @throws FlightBookingSystemException if the date is invalid after the allowed attempts
     */
    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts) throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher than 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print("Departure Date (YYYY-MM-DD): ");
            try {
                LocalDate departureDate = LocalDate.parse(br.readLine());
                return departureDate;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }
        throw new FlightBookingSystemException("Incorrect departure date provided. Cannot create flight.");
    }

    /**
     * Parses a date from the user input with a default of 3 attempts.
     *
     * @param br the BufferedReader to read user input
     * @return the parsed LocalDate
     * @throws IOException if an I/O error occurs
     * @throws FlightBookingSystemException if the date is invalid after 3 attempts
     */
    private static LocalDate parseDateWithAttempts(BufferedReader br) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}