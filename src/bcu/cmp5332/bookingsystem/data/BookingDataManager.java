package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Manages the loading and storing of booking data for the Flight Booking System.
 * Implements the DataManager interface.
 */
public class BookingDataManager implements DataManager {
    
    private final String RESOURCE;
    
    public BookingDataManager() {
        // Get the absolute path relative to the project root
        String projectRoot = getProjectRoot();
        RESOURCE = projectRoot + "/resources/data/bookings.txt";
    }
    
    private String getProjectRoot() {
        // Get the location of the class file and navigate to project root
        String classPath = BookingDataManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // Handle Windows paths (remove leading slash if present)
        if (classPath.startsWith("/") && classPath.contains(":")) {
            classPath = classPath.substring(1);
        }
        // Decode URL encoding (e.g., %20 for spaces)
        try {
            classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
        } catch (Exception e) {
            // Ignore decoding errors
        }
        java.io.File classDir = new java.io.File(classPath);
        // Navigate up to find project root (look for resources folder)
        java.io.File current = classDir.isDirectory() ? classDir : classDir.getParentFile();
        while (current != null) {
            java.io.File resourcesDir = new java.io.File(current, "resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                return current.getAbsolutePath().replace("\\", "/");
            }
            current = current.getParentFile();
        }
        // Fallback to current working directory
        return System.getProperty("user.dir").replace("\\", "/");
    }
    
    /**
     * Loads booking data from the file and populates the FlightBookingSystem.
     * 
     * @param fbs The FlightBookingSystem instance where bookings will be loaded.
     * @throws IOException If there is an error accessing the file.
     * @throws FlightBookingSystemException If there is an issue parsing booking data.
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(RESOURCE);
        if (!file.exists()) return;
        
        try (Scanner sc = new Scanner(file)) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    line_idx++;
                    continue;
                }
                
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int bookingId = Integer.parseInt(properties[0]);
                    int customerId = Integer.parseInt(properties[1]);
                    int flightId = Integer.parseInt(properties[2]);
                    LocalDate bookingDate = LocalDate.parse(properties[3]);
                    double bookingFee = Double.parseDouble(properties[4]);
                    
                    Customer customer;
                    try {
                        customer = fbs.getCustomerByID(customerId);
                    } catch (FlightBookingSystemException ex) {
                        System.err.println("Booking on line " + line_idx + " cancelled: " + ex.getMessage());
                        line_idx++;
                        continue;
                    }
                    
                    Flight flight;
                    try {
                        flight = fbs.getFlightByID(flightId);
                    } catch (FlightBookingSystemException ex) {
                        System.err.println("Booking on line " + line_idx + " cancelled: " + ex.getMessage());
                        Booking cancelledBooking = new Booking(bookingId, customer, new Flight(flightId, "N/A", "N/A", "N/A", LocalDate.now(), 0, 0), bookingDate, bookingFee);
                        cancelledBooking.cancel();
                        fbs.addBookingFromData(cancelledBooking);
                        line_idx++;
                        continue;
                    }
                    
                    Booking booking = new Booking(bookingId, customer, flight, bookingDate, bookingFee);
                    fbs.addBookingFromData(booking);
                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error loading booking on line " + line_idx + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }
    
    /**
     * Stores the booking data from the FlightBookingSystem to a file.
     * 
     * @param fbs The FlightBookingSystem instance containing booking data to store.
     * @throws IOException If there is an error writing to the file.
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Booking booking : fbs.getBookings()) {
                out.print(booking.getId() + SEPARATOR);
                out.print(booking.getCustomer().getId() + SEPARATOR);
                out.print(booking.getFlight().getId() + SEPARATOR);
                out.print(booking.getBookingDate() + SEPARATOR);
                out.print(booking.getBookingFee() + SEPARATOR);
                out.println();
            }
        }
    }
}