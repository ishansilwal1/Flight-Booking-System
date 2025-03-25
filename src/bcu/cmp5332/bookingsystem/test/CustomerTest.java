package bcu.cmp5332.bookingsystem.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import java.util.List;

public class CustomerTest {
    private Customer customer;
    private Flight flight;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, "Alice", "123456789", "alice@example.com", "alicePass");
        flight = new Flight(1, "FL123", "Origin", "Destination", java.time.LocalDate.now().plusDays(10), 100.0, 150);
        booking1 = new Booking(1, customer, flight, java.time.LocalDate.now(), 125.0);
        booking2 = new Booking(2, customer, flight, java.time.LocalDate.now(), 130.0);
    }

    @Test
    public void testAddBooking() {
        assertTrue(customer.getBookings().isEmpty());
        customer.addBooking(booking1);
        List<Booking> bookings = customer.getBookings();
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void testCancelBooking() {
        customer.addBooking(booking1);
        customer.addBooking(booking2);
        assertEquals(2, customer.getBookings().size());
        customer.cancelBooking(booking1);
        List<Booking> bookings = customer.getBookings();
        assertEquals(1, bookings.size());
        assertFalse(bookings.contains(booking1));
    }

    @Test
    public void testGetDetailsShort() {
        String details = customer.getDetailsShort();
        assertTrue(details.contains("Alice"));
        assertTrue(details.contains("123456789"));
    }
}