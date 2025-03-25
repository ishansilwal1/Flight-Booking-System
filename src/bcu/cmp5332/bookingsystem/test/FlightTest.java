package bcu.cmp5332.bookingsystem.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import java.time.LocalDate;
import java.util.List;

public class FlightTest {
    private Flight flight;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    public void setUp() {
        // Flight departing in 10 days, base price 100, capacity 2.
        flight = new Flight(1, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 2);
        customer1 = new Customer(1, "Alice", "123", "alice@example.com", "pass");
        customer2 = new Customer(2, "Bob", "456", "bob@example.com", "pass");
    }

    @Test
    public void testCalculatePrice() {
        // If booking is made now, days left = 10 so 25% increase should apply.
        double price = flight.calculatePrice(LocalDate.now());
        assertEquals(100.0 * 1.25, price, 0.001);

        // If booking is made 8 days later, then days left = 2, so 50% increase.
        double price2 = flight.calculatePrice(LocalDate.now().plusDays(8));
        assertEquals(100.0 * 1.50, price2, 0.001);
    }

    @Test
    public void testAddPassengerAndCapacity() {
        assertTrue(flight.addPassenger(customer1));
        assertTrue(flight.addPassenger(customer2));
        // Capacity is 2; trying to add a third passenger should fail.
        Customer customer3 = new Customer(3, "Charlie", "789", "charlie@example.com", "pass");
        assertFalse(flight.addPassenger(customer3));
    }

    @Test
    public void testGetDetailsShort() {
        String details = flight.getDetailsShort();
        assertTrue(details.contains("FL123"));
        assertTrue(details.contains("Origin"));
        assertTrue(details.contains("Destination"));
    }

    @Test
    public void testGetPassengers() {
        flight.addPassenger(customer1);
        flight.addPassenger(customer2);
        List<Customer> passengers = flight.getPassengers();
        assertEquals(2, passengers.size());
    }
}