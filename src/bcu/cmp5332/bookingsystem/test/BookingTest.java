package bcu.cmp5332.bookingsystem.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

public class BookingTest {
    private Customer customer;
    private Flight flight;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, "Test User", "123456", "test@example.com", "password");
        flight = new Flight(1, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 100);
        booking = new Booking(1, customer, flight, LocalDate.now(), 100.0);
    }

    @Test
    public void testBookingToString() {
        String str = booking.toString();
        // Check that the toString() contains customer's name and flight number.
        assertTrue(str.contains("Test User"));
        assertTrue(str.contains("FL123"));
    }

    @Test
    public void testCancel() {
        // Before cancellation, isCancelled() should return false.
        assertFalse(booking.isCancelled());
        booking.cancel();
        // After cancellation, isCancelled() should return true.
        assertTrue(booking.isCancelled());
    }
}