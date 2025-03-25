package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * The FlightDataManager class is responsible for loading and storing flight data
 * to and from a file. It implements the DataManager interface and handles the
 * specific details of reading and writing flight information.
 */
public class FlightDataManager implements DataManager {

    // File path for the flight data resource
    private final String RESOURCE = "./resources/data/flights.txt";

    /**
     * Loads flight data from the specified file into the FlightBookingSystem.
     * The method reads the file line by line, parses the flight properties,
     * and creates Flight objects to add to the system.
     *
     * @param fbs The FlightBookingSystem instance to which the flight data will be loaded.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws FlightBookingSystemException If there is an error parsing the flight data.
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(RESOURCE);
        if (!file.exists()) return; // If the file does not exist, exit the method

        try (Scanner sc = new Scanner(file)) {
            int line_idx = 1; // Tracks the current line number for error reporting
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    line_idx++;
                    continue; // Skip empty lines
                }

                String[] properties = line.split(SEPARATOR, -1); // Split the line into properties
                try {
                    // Parse flight properties
                    int id = Integer.parseInt(properties[0]);
                    String flightNumber = properties[1];
                    String origin = properties[2];
                    String destination = properties[3];
                    LocalDate departureDate = LocalDate.parse(properties[4]);
                    double basePrice = properties.length > 5 && !properties[5].isEmpty() ? Double.parseDouble(properties[5]) : 100.0;
                    int capacity = properties.length > 6 && !properties[6].isEmpty() ? Integer.parseInt(properties[6]) : 150;
                    boolean isDeleted = properties.length > 7 && !properties[7].isEmpty() ? Boolean.parseBoolean(properties[7]) : false;

                    // Create a new Flight object and add it to the system
                    Flight flight = new Flight(id, flightNumber, origin, destination, departureDate, basePrice, capacity);
                    flight.setDeleted(isDeleted);
                    fbs.addFlight(flight);
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse flight data on line " + line_idx + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }

    /**
     * Stores flight data from the FlightBookingSystem into the specified file.
     * The method writes each flight's properties to the file in a formatted manner.
     *
     * @param fbs The FlightBookingSystem instance from which the flight data will be stored.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Flight flight : fbs.getAllFlights()) {
                // Write flight properties to the file
                out.print(flight.getId() + SEPARATOR);
                out.print(flight.getFlightNumber() + SEPARATOR);
                out.print(flight.getOrigin() + SEPARATOR);
                out.print(flight.getDestination() + SEPARATOR);
                out.print(flight.getDepartureDate() + SEPARATOR);
                out.print(flight.getBasePrice() + SEPARATOR);
                out.print(flight.getCapacity() + SEPARATOR);
                out.print(flight.isDeleted() + SEPARATOR);
                out.println(); // Move to the next line for the next flight
            }
        }
    }
}