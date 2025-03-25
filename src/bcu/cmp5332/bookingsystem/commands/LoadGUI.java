package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.gui.LoginWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import javax.swing.SwingUtilities;

/**
 * Command implementation that launches the graphical user interface (GUI) 
 * for the flight booking system. This command initializes the login window
 * and ensures it runs on the Event Dispatch Thread using SwingUtilities.
 * 
 * @version 1.0
 * @see Command
 * @see FlightBookingSystem
 * @see LoginWindow
 * @see SwingUtilities#invokeLater(Runnable)
 */
public class LoadGUI implements Command {
    
    /**
     * Executes the LoadGUI command.
     * Creates a new LoginWindow instance and ensures it is launched on the
     * Swing Event Dispatch Thread using SwingUtilities.invokeLater().
     * This is necessary to ensure thread safety in Swing applications.
     *
     * @param flightBookingSystem The FlightBookingSystem instance to be used by the GUI
     * @throws FlightBookingSystemException if there is an error during GUI initialization
     * @see LoginWindow
     * @see SwingUtilities#invokeLater(Runnable)
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        SwingUtilities.invokeLater(() -> new LoginWindow(flightBookingSystem));
    }
}