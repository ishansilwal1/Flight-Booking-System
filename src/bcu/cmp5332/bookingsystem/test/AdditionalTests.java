package bcu.cmp5332.bookingsystem.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;
import java.util.List;

public class AdditionalTests {
    private FlightBookingSystem system;
    private Flight flight;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    public void setup() throws FlightBookingSystemException {
        system = new FlightBookingSystem();
        // Create a flight departing in 10 days with base price 100 and capacity 1 (for capacity tests)
        flight = new Flight(1, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 1);
        system.addFlight(flight);
        // Create two customers
        customer1 = new Customer(1, "Alice", "111111", "alice@example.com", "pass1");
        customer2 = new Customer(2, "Bob", "222222", "bob@example.com", "pass2");
        system.addCustomer(customer1);
        system.addCustomer(customer2);
    }

    @Test
    public void testDuplicateFlight() {
        // Creating a flight with the same flight number and departure date should fail
        Flight duplicateFlight = new Flight(2, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 1);
        Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
            system.addFlight(duplicateFlight);
        });
        String expectedMessage = "A flight with the same number and date exists";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDuplicateCustomer() {
        // Attempt to add a customer with the same name or email should throw an exception.
        Customer duplicate = new Customer(3, "Alice", "333333", "alice@example.com", "pass3");
        Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
            system.addCustomer(duplicate);
        });
        String expectedMessage1 = "Customer name already exists";
        String expectedMessage2 = "Customer email already exists";
        assertTrue(exception.getMessage().contains(expectedMessage1) || exception.getMessage().contains(expectedMessage2));
    }

    @Test
    public void testFlightCapacity() throws FlightBookingSystemException {
        // Flight capacity is 1, so the first booking should succeed.
        Booking booking1 = system.addBooking(customer1.getId(), flight.getId(), LocalDate.now());
        assertNotNull(booking1);
        // Trying to add a second booking on the same flight should throw an exception.
        Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
            system.addBooking(customer2.getId(), flight.getId(), LocalDate.now());
        });
        String expectedMessage = "Flight is at full capacity";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testDynamicPricing() {
        // Test dynamic pricing based on days left.
        // If booking is made now, days left = 10 so a 25% increase should be applied.
        double priceNow = flight.calculatePrice(LocalDate.now());
        assertEquals(100.0 * 1.25, priceNow, 0.001);
        // If booking is made 8 days later, days left becomes 2 (10-8=2) so a 50% increase should apply.
        double priceLater = flight.calculatePrice(LocalDate.now().plusDays(8));
        assertEquals(100.0 * 1.50, priceLater, 0.001);
    }

    @Test
    public void testCancelBookingRestoresCapacity() throws FlightBookingSystemException {
        // Add a booking, then cancel it, and verify that the flight capacity is restored.
        Booking booking1 = system.addBooking(customer1.getId(), flight.getId(), LocalDate.now());
        assertEquals(1, flight.getPassengers().size());
        system.cancelBooking(booking1.getId(), 10.0);
        assertEquals(0, flight.getPassengers().size());
        // Verify that the booking moved to cancelledBookings
        List<Booking> cancelled = system.getCancelledBookings();
        assertTrue(cancelled.contains(booking1));
    }

    @Test
    public void testRemoveCustomerCascadesCancellations() throws FlightBookingSystemException {
        // Add a booking for customer2, then remove customer2.
        // Reset flight capacity to 1 for clarity.
        flight = new Flight(1, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 1);
        system = new FlightBookingSystem();
        system.addFlight(flight);
        system.addCustomer(customer2);
        Booking booking2 = system.addBooking(customer2.getId(), flight.getId(), LocalDate.now());
        assertEquals(1, flight.getPassengers().size());
        system.removeCustomer(customer2.getId());
        // After removal, active bookings should be 0 and cancelledBookings should include the booking.
        assertEquals(0, system.getBookings().size());
        List<Booking> cancelled = system.getCancelledBookings();
        assertTrue(cancelled.contains(booking2));
        // Also, customer2 should be marked as deleted.
        Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
            system.getCustomerByID(customer2.getId());
        });
        assertTrue(exception.getMessage().contains("No customer with that ID"));
    }
    
    @Test
    public void testAddBookingFromData() throws FlightBookingSystemException {
        // Create a booking manually and add via addBookingFromData
        Booking booking = new Booking(10, customer1, flight, LocalDate.now(), 120.0);
        system.addBookingFromData(booking);
        List<Booking> allBookings = system.getBookings();
        // Check that the booking is present
        assertTrue(allBookings.stream().anyMatch(b -> b.getId() == 10));
    }
}