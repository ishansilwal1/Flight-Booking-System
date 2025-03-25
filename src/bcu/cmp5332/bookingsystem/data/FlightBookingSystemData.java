package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The FlightBookingSystemData class is responsible for managing the loading and storing
 * of data for the FlightBookingSystem. It uses a list of DataManager implementations
 * to handle specific data types such as flights, customers, and bookings.
 */
public class FlightBookingSystemData {

    // List of DataManager implementations to handle different types of data
    private static final List<DataManager> dataManagers = new ArrayList<>();

    // Static block to initialize the list of DataManagers
    static {
        dataManagers.add(new FlightDataManager());
        dataManagers.add(new CustomerDataManager());
        dataManagers.add(new BookingDataManager());
    }

    /**
     * Loads all data into the FlightBookingSystem by delegating to the registered DataManagers.
     * Each DataManager is responsible for loading its specific type of data (e.g., flights, customers, bookings).
     *
     * @return A fully populated FlightBookingSystem instance.
     * @throws FlightBookingSystemException If an error occurs while loading the data.
     * @throws IOException If an I/O error occurs during the data loading process.
     */
    public static FlightBookingSystem load() throws FlightBookingSystemException, IOException {
        FlightBookingSystem fbs = new FlightBookingSystem();
        for (DataManager dm : dataManagers) {
            try {
                dm.loadData(fbs);
            } catch (Exception e) {
                // Print the stack trace for debugging purposes
                e.printStackTrace();
            }
        }
        return fbs;
    }

    /**
     * Stores all data from the FlightBookingSystem by delegating to the registered DataManagers.
     * Each DataManager is responsible for storing its specific type of data (e.g., flights, customers, bookings).
     *
     * @param fbs The FlightBookingSystem instance containing the data to be stored.
     * @throws IOException If an I/O error occurs during the data storing process.
     */
    public static void store(FlightBookingSystem fbs) throws IOException {
        for (DataManager dm : dataManagers) {
            try {
                dm.storeData(fbs);
            } catch (Exception e) {
                // Print the stack trace for debugging purposes
                e.printStackTrace();
                throw new IOException("Failed to store data: " + e.getMessage(), e);
            }
        }
    }
}
