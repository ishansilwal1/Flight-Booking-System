package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the flight booking system, managing customers, flights, and bookings.
 */
public class FlightBookingSystem {
    private final LocalDate systemDate = LocalDate.now();
    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final Map<Integer, Booking> bookings = new TreeMap<>();
    // Map for cancelled bookings (kept separately)
    private final Map<Integer, Booking> cancelledBookings = new TreeMap<>();

    /**
     * Gets the current system date.
     *
     * @return the system date
     */
    public LocalDate getSystemDate() { return systemDate; }

    /**
     * Gets the list of active flights that have not departed yet.
     *
     * @return the list of active flights
     */
    public List<Flight> getFlights() {
        return flights.values().stream()
                .filter(f -> !f.isDeleted() && !f.getDepartureDate().isBefore(systemDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the list of all flights, including deleted ones.
     *
     * @return the list of all flights
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }
    
    /**
     * Gets a flight by its ID.
     *
     * @param id the flight ID
     * @return the flight with the specified ID
     * @throws FlightBookingSystemException if no flight with the specified ID is found
     */
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight f = flights.get(id);
        if (f == null || f.isDeleted()) {
            throw new FlightBookingSystemException("No flight with that ID.");
        }
        return f;
    }
    
    /**
     * Gets the list of active customers.
     *
     * @return the list of active customers
     */
    public List<Customer> getCustomers() {
        return customers.values().stream()
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the list of all customers, including deleted ones.
     *
     * @return the list of all customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }
    
    /**
     * Gets a customer by their ID.
     *
     * @param id the customer ID
     * @return the customer with the specified ID
     * @throws FlightBookingSystemException if no customer with the specified ID is found
     */
    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        Customer c = customers.get(id);
        if (c == null || c.isDeleted()) {
            throw new FlightBookingSystemException("No customer with that ID.");
        }
        return c;
    }
    
    /**
     * Adds a new flight to the system.
     *
     * @param flight the flight to add
     * @throws FlightBookingSystemException if a flight with the same ID or flight number and date already exists
     */
    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.containsKey(flight.getId())) {
            throw new FlightBookingSystemException("Duplicate flight ID.");
        }
        for (Flight existing : flights.values()) {
            if (existing.getFlightNumber().equals(flight.getFlightNumber()) &&
                existing.getDepartureDate().isEqual(flight.getDepartureDate()) &&
                !existing.isDeleted()) {
                throw new FlightBookingSystemException("A flight with the same number and date exists.");
            }
        }
        flights.put(flight.getId(), flight);
    }
    
    /**
     * Adds a new customer to the system.
     * Ensures no active customer shares the same name or email.
     *
     * @param customer the customer to add
     * @throws FlightBookingSystemException if a customer with the same name, email, or ID already exists
     */
    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        for (Customer c : customers.values()) {
            if (!c.isDeleted()) {
                if (c.getName().equalsIgnoreCase(customer.getName())) {
                    throw new FlightBookingSystemException("Customer name already exists.");
                }
                if (c.getEmail().equalsIgnoreCase(customer.getEmail())) {
                    throw new FlightBookingSystemException("Customer email already exists.");
                }
            }
        }
        if (customers.containsKey(customer.getId())) {
            throw new FlightBookingSystemException("Duplicate customer ID.");
        }
        customers.put(customer.getId(), customer);
    }
    
    /**
     * Gets the list of all bookings.
     *
     * @return the list of all bookings
     */
    public List<Booking> getBookings() {
        return new ArrayList<>(bookings.values());
    }
    
    /**
     * Gets the list of cancelled bookings.
     *
     * @return the list of cancelled bookings
     */
    public List<Booking> getCancelledBookings() {
        return new ArrayList<>(cancelledBookings.values());
    }
    
    /**
     * Adds a new booking to the system.
     *
     * @param customerId  the ID of the customer making the booking
     * @param flightId    the ID of the flight being booked
     * @param bookingDate the date the booking is made
     * @return the created booking
     * @throws FlightBookingSystemException if the customer or flight is not found, or if the flight is at full capacity
     */
    public Booking addBooking(int customerId, int flightId, LocalDate bookingDate) throws FlightBookingSystemException {
        Customer customer = getCustomerByID(customerId);
        Flight flight = getFlightByID(flightId);
        if (flight.getPassengers().size() >= flight.getCapacity()) {
            throw new FlightBookingSystemException("Flight is at full capacity.");
        }
        int bookingMaxId = bookings.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int newId = bookingMaxId + 1;
        double fee = flight.calculatePrice(bookingDate);
        Booking booking = new Booking(newId, customer, flight, bookingDate, fee);
        bookings.put(newId, booking);
        customer.addBooking(booking);
        if (!flight.addPassenger(customer)) {
            throw new FlightBookingSystemException("Failed to add passenger due to capacity issues.");
        }
        return booking;
    }
    
    /**
     * Cancels a booking and marks it as cancelled.
     *
     * @param bookingId       the ID of the booking to cancel
     * @param cancellationFee the cancellation fee
     * @throws FlightBookingSystemException if no booking with the specified ID is found
     */
    public void cancelBooking(int bookingId, double cancellationFee) throws FlightBookingSystemException {
        if (!bookings.containsKey(bookingId)) {
            throw new FlightBookingSystemException("No booking with that ID.");
        }
        Booking booking = bookings.get(bookingId);
        double refund = booking.getBookingFee() - cancellationFee;
        if (refund < 0) refund = 0;
        System.out.println("Refund amount: $" + refund);
        booking.cancel();
        bookings.remove(bookingId);
        cancelledBookings.put(bookingId, booking);
    }
    
    /**
     * Removes a customer from the system and cancels all their bookings.
     *
     * @param customerId the ID of the customer to remove
     * @throws FlightBookingSystemException if no customer with the specified ID is found
     */
    public void removeCustomer(int customerId) throws FlightBookingSystemException {
        Customer customer = getCustomerByID(customerId);
        List<Booking> customerBookings = new ArrayList<>(customer.getBookings());
        for (Booking booking : customerBookings) {
            cancelBooking(booking.getId(), 0.0);
        }
        customer.setDeleted(true);
    }
    
    /**
     * Removes a flight from the system.
     *
     * @param flightId the ID of the flight to remove
     * @throws FlightBookingSystemException if no flight with the specified ID is found
     */
    public void removeFlight(int flightId) throws FlightBookingSystemException {
        Flight flight = getFlightByID(flightId);
        flight.setDeleted(true);
    }
    
    /**
     * Adds a booking from data (used for loading data).
     *
     * @param booking the booking to add
     * @throws FlightBookingSystemException if a booking with the same ID already exists
     */
    public void addBookingFromData(Booking booking) throws FlightBookingSystemException {
        if (bookings.containsKey(booking.getId())) {
            throw new FlightBookingSystemException("Duplicate booking ID in data.");
        }
        bookings.put(booking.getId(), booking);
        booking.getCustomer().addBooking(booking);
        booking.getFlight().addPassenger(booking.getCustomer());
    }
    
    /**
     * Gets a booking by its ID.
     *
     * @param id the booking ID
     * @return the booking with the specified ID
     * @throws FlightBookingSystemException if no booking with the specified ID is found
     */
    public Booking getBookingByID(int id) throws FlightBookingSystemException {
        if (!bookings.containsKey(id)) {
            throw new FlightBookingSystemException("No booking with that ID.");
        }
        return bookings.get(id);
    }
    
    /**
     * Deletes a flight from the system.
     *
     * @param flightId the ID of the flight to be deleted
     * @throws FlightBookingSystemException if the flight is not found
     */
    public void deleteFlight(int flightId) throws FlightBookingSystemException {
        Flight flight = getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight not found.");
        }
        List<Booking> flightBookings = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getFlight().equals(flight)) {
                flightBookings.add(booking);
            }
        }
        for (Booking booking : flightBookings) {
            bookings.remove(booking.getId());
        }
        flights.remove(flightId);
        writeFlightsToFile();
    }
    
    /**
     * Deletes a customer from the system.
     *
     * @param customerId the ID of the customer to be deleted
     * @throws FlightBookingSystemException if the customer is not found
     */
    public void deleteCustomer(int customerId) throws FlightBookingSystemException {
        Customer customer = getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer not found.");
        }
        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getCustomer().equals(customer)) {
                customerBookings.add(booking);
            }
        }
        for (Booking booking : customerBookings) {
            bookings.remove(booking.getId());
        }
        customers.remove(customerId);
        writeCustomersToFile();
    }
    
    /**
     * Writes the customer data to a file.
     */
    private void writeCustomersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/data/customers.txt"))) {
            for (Customer customer : customers.values()) {
                writer.write(customer.getId() + "," + customer.getName() + "," + customer.getPhone() + "," + customer.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to customers.txt: " + e.getMessage());
        }
    }
    
    /**
     * Writes the flight data to a file.
     */
    private void writeFlightsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/data/flights.txt"))) {
            for (Flight flight : flights.values()) {
                writer.write(flight.getId() + "," + flight.getFlightNumber() + "," + flight.getOrigin() + "," + flight.getDestination() + "," + flight.getDepartureDate() + "," + flight.getCapacity() + "," + flight.getBasePrice());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to flights.txt: " + e.getMessage());
        }
    }
}