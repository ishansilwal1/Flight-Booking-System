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
	
	public class FlightBookingSystemTest {
	    private FlightBookingSystem system;
	    private Flight flight;
	    private Customer customer;
	
	    @BeforeEach
	    public void setUp() throws FlightBookingSystemException {
	        system = new FlightBookingSystem();
	        flight = new Flight(1, "FL123", "Origin", "Destination", LocalDate.now().plusDays(10), 100.0, 2);
	        customer = new Customer(1, "Test User", "123456", "test@example.com", "password");
	        system.addFlight(flight);
	        system.addCustomer(customer);
	    }
	
	    @Test
	    public void testAddBooking() throws FlightBookingSystemException {
	        Booking booking = system.addBooking(customer.getId(), flight.getId(), LocalDate.now());
	        assertNotNull(booking);
	        assertEquals(1, system.getBookings().size());
	        assertEquals(1, flight.getPassengers().size());
	    }
	
	    @Test
	    public void testCancelBooking() throws FlightBookingSystemException {
	        Booking booking = system.addBooking(customer.getId(), flight.getId(), LocalDate.now());
	        int bookingId = booking.getId();
	        system.cancelBooking(bookingId, 10.0);
	        assertEquals(0, system.getBookings().size());
	        assertEquals(1, system.getCancelledBookings().size());
	        // Customer should no longer be in flight's passenger list.
	        assertFalse(flight.getPassengers().contains(customer));
	    }
	
	    @Test
	    public void testRemoveCustomerCancelsBookings() throws FlightBookingSystemException {
	        system.addBooking(customer.getId(), flight.getId(), LocalDate.now());
	        assertEquals(1, system.getBookings().size());
	        system.removeCustomer(customer.getId());
	        // After removal, active bookings should be 0 and cancelledBookings should have at least one booking.
	        assertEquals(0, system.getBookings().size());
	        assertTrue(system.getCancelledBookings().size() > 0);
	        // Customer should be marked as deleted.
	        assertTrue(customer.isDeleted());
	    }
	
	    @Test
	    public void testRemoveFlight() throws FlightBookingSystemException {
	        system.removeFlight(flight.getId());
	        assertTrue(flight.isDeleted());
	        Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
	            system.getFlightByID(flight.getId());
	        });
	        assertTrue(exception.getMessage().contains("No flight with that ID"));
	    }
	}