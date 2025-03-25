package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a flight in the flight booking system.
 */
public class Flight {
    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private double basePrice;
    private int capacity;
    private boolean isDeleted = false;
    private final Set<Customer> passengers;

    /**
     * Constructs a new Flight with the specified details.
     *
     * @param id            the unique identifier for the flight
     * @param flightNumber  the flight number
     * @param origin        the origin of the flight
     * @param destination   the destination of the flight
     * @param departureDate the departure date of the flight
     * @param basePrice     the base price of the flight
     * @param capacity      the capacity of the flight
     */
    public Flight(int id, String flightNumber, String origin, String destination,
                  LocalDate departureDate, double basePrice, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.passengers = new HashSet<>();
    }

    // Getters and setters

    /**
     * Gets the unique identifier of the flight.
     *
     * @return the flight ID
     */
    public int getId() { return id; }

    /**
     * Gets the flight number.
     *
     * @return the flight number
     */
    public String getFlightNumber() { return flightNumber; }

    /**
     * Gets the origin of the flight.
     *
     * @return the origin of the flight
     */
    public String getOrigin() { return origin; }

    /**
     * Gets the destination of the flight.
     *
     * @return the destination of the flight
     */
    public String getDestination() { return destination; }

    /**
     * Gets the departure date of the flight.
     *
     * @return the departure date of the flight
     */
    public LocalDate getDepartureDate() { return departureDate; }

    /**
     * Gets the base price of the flight.
     *
     * @return the base price of the flight
     */
    public double getBasePrice() { return basePrice; }

    /**
     * Gets the capacity of the flight.
     *
     * @return the capacity of the flight
     */
    public int getCapacity() { return capacity; }

    /**
     * Checks if the flight is marked as deleted.
     *
     * @return true if the flight is deleted, false otherwise
     */
    public boolean isDeleted() { return isDeleted; }

    /**
     * Marks the flight as deleted or not deleted.
     *
     * @param deleted true to mark the flight as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }

    /**
     * Calculates the price of the flight based on the booking date.
     * If the booking is made within 7 days of departure, the price increases by 50%.
     * If the booking is made within 14 days of departure, the price increases by 25%.
     *
     * @param bookingDate the date the booking is made
     * @return the calculated price of the flight
     */
    public double calculatePrice(LocalDate bookingDate) {
        long daysLeft = ChronoUnit.DAYS.between(bookingDate, departureDate);
        double price = basePrice;
        if (daysLeft <= 7) {
            price *= 1.50;
        } else if (daysLeft <= 14) {
            price *= 1.25;
        }
        return price;
    }

    /**
     * Adds a passenger to the flight if there is available capacity.
     *
     * @param passenger the passenger to add
     * @return true if the passenger was added, false otherwise
     */
    public boolean addPassenger(Customer passenger) {
        if (passengers.size() < capacity) {
            return passengers.add(passenger);
        }
        return false;
    }

    /**
     * Gets the list of passengers on the flight.
     *
     * @return a copy of the list of passengers
     */
    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Gets a short description of the flight's details.
     *
     * @return a string containing the flight's ID, flight number, origin, destination, departure date, base price, and capacity
     */
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " + destination +
               " on " + departureDate.format(dtf) + ", Base Price: $" + basePrice + ", Capacity: " + capacity;
    }

    /**
     * Gets a detailed description of the flight's details, including the list of passengers.
     *
     * @return a string containing the flight's details and the list of passengers
     */
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight ID: ").append(id)
          .append("\nFlight Number: ").append(flightNumber)
          .append("\nOrigin: ").append(origin)
          .append("\nDestination: ").append(destination)
          .append("\nDeparture Date: ").append(departureDate)
          .append("\nBase Price: $").append(basePrice)
          .append("\nCapacity: ").append(capacity)
          .append("\nPassengers: ");
        if (passengers.isEmpty()) {
            sb.append("None");
        } else {
            for (Customer c : passengers) {
                sb.append(c.getName()).append(" (").append(c.getPhone()).append("), ");
            }
            if(sb.length()>=2) sb.setLength(sb.length()-2);
        }
        return sb.toString();
    }
}