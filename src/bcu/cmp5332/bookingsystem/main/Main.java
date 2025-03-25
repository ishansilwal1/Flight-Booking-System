package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The main class for the Flight Booking System application.
 * This class initializes the system and provides a command-line interface for user interaction.
 * 
 * @author 
 * Ishan Silwal
 * Animesh Maharjan
 */
public class Main {
    /**
     * The main method that starts the Flight Booking System application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            FlightBookingSystem fbs = FlightBookingSystemData.load();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Welcome to the ISAM Airlines");
            System.out.println("Type 'help' for available commands, or 'exit'/'quit' to close the application.");
            
            while (true) {
                System.out.print("> ");
                String line = br.readLine();
                if (line == null || line.trim().equalsIgnoreCase("exit") || line.trim().equalsIgnoreCase("quit")) {
                    System.out.println("Exited...");
                    break;
                }
                
                try {
                    // Parse the entered command using your CommandParser
                    bcu.cmp5332.bookingsystem.commands.Command command = CommandParser.parse(line);
                    // Execute the command (make sure your Command interface has an execute method)
                    command.execute(fbs);
                } catch (Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            }
            
            // Optionally, save the system data on exit.
            FlightBookingSystemData.store(fbs);
        } catch (Exception e) {
            System.err.println("Failed to initialize system: " + e.getMessage());
        }
    }
}