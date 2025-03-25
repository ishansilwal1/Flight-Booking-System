package bcu.cmp5332.bookingsystem.data;

/**
 * The DataManager interface defines the methods required for loading and storing data
 * for the FlightBookingSystem. Implementing classes should handle the specific details
 * of data persistence, such as reading from and writing to files or databases.
 */
public interface DataManager {

    /**
     * A constant string used as a separator when parsing or formatting data.
     */
    public static final String SEPARATOR = "::";

    /**
     * Loads data into the FlightBookingSystem from a persistent storage.
     * Implementing classes should define how the data is retrieved and populated
     * into the system.
     *
     * @param fbs The FlightBookingSystem instance to which the data will be loaded.
     * @throws Exception If an error occurs during the data loading process.
     */
    void loadData(bcu.cmp5332.bookingsystem.model.FlightBookingSystem fbs) throws Exception;

    /**
     * Stores data from the FlightBookingSystem into a persistent storage.
     * Implementing classes should define how the data is saved and where it is stored.
     *
     * @param fbs The FlightBookingSystem instance from which the data will be stored.
     * @throws Exception If an error occurs during the data storing process.
     */
    void storeData(bcu.cmp5332.bookingsystem.model.FlightBookingSystem fbs) throws Exception;
}