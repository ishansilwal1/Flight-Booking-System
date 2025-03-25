package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This class represents the main window of the flight booking system.
 * It provides the user interface for managing flights, bookings, and customers.
 */
public class MainWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final FlightBookingSystem fbs;
    private JTable currentTable;
    private boolean isAdmin;
    private Integer loggedInCustomerId;

    private JMenuBar menuBar;
    private JMenu adminMenu, flightsMenu, bookingsMenu, customersMenu;
    private JMenuItem adminExit;
    private JMenuItem flightsViewUpcoming, flightsViewAll, flightsAdd, flightsDel;
    private JMenuItem bookingsView, bookingsIssue, bookingsUpdate, bookingsCancel, bookingsViewAllCombined;

    /**
     * Constructs a new MainWindow.
     *
     * @param fbs the flight booking system instance
     */
    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        applyCustomUI();
        initialize();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    FlightBookingSystemData.store(fbs);
                } catch (IOException ex) {
                    System.err.println("Error saving data on exit: " + ex.getMessage());
                } finally {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Applies custom UI settings to the components.
     */
    private void applyCustomUI() {
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 18));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 18));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 18));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 18));
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Panel.background", Color.WHITE);

        // Primary button color (orange)
        UIManager.put("Button.background", new Color(0xFF9800));
        UIManager.put("Button.foreground", Color.WHITE);

        // Secondary button color (blue)
        UIManager.put("Button.select", new Color(0x1976D2));
    }

    /**
     * Loads and scales an icon from the specified file path.
     *
     * @param path the file path of the icon
     * @param width the desired width of the icon
     * @param height the desired height of the icon
     * @return the scaled ImageIcon
     */
    private ImageIcon loadScaledIcon(String path, int width, int height) {
        File f = new File(path);
        if (!f.exists()) {
            System.err.println("Icon file not found: " + path);
        }
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Flight Booking Management System");
        setSize(1000, 600);
        getContentPane().setBackground(new Color(0xF5F5F5)); // Set background color to light gray
        initMenuBar();
        addWelcomeMessage(); // Add welcome message
        createSidebarMenu(); // Add sidebar menu
        addCommonLayout();
    }

    /**
     * Adds a welcome message to the window.
     */
    private void addWelcomeMessage() {
        JLabel welcomeLabel = new JLabel("Welcome to ISAM Airlines", JLabel.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0x1E3A5F)); // Dark blue color
        getContentPane().add(welcomeLabel, BorderLayout.NORTH);
    }

    /**
     * Initializes the menu bar and its items.
     */
    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0x1E3A5F)); // Dark blue background
        menuBar.setForeground(Color.WHITE); // White text
        menuBar.setPreferredSize(new Dimension(getWidth(), 50)); // Increase height of the menu bar

        Font menuFont = new Font("SansSerif", Font.BOLD, 18); // Bigger font for menu items

        // Admin Menu
        adminMenu = new JMenu("Admin");
        adminMenu.setForeground(Color.WHITE); // White text
        adminMenu.setFont(menuFont);
        adminMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAdmin) {
                    displayAdminForm();
                }
            }
        });
        menuBar.add(adminMenu);

        // Flights Menu
        flightsMenu = new JMenu("Flights");
        flightsMenu.setForeground(Color.WHITE); // White text
        flightsMenu.setFont(menuFont);
        flightsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayFlightsForm();
            }
        });

        flightsAdd = new JMenuItem("Add Flight");
        flightsAdd.setFont(menuFont);
        flightsAdd.addActionListener(e -> new AddFlightWindow(this));
        flightsMenu.add(flightsAdd);

        flightsDel = new JMenuItem("Delete Flight");
        flightsDel.setFont(menuFont);
        flightsDel.addActionListener(e -> deleteSelectedFlight());
        flightsMenu.add(flightsDel);

        menuBar.add(flightsMenu);

        // Bookings Menu
        bookingsMenu = new JMenu("Bookings");
        bookingsMenu.setForeground(Color.WHITE); // White text
        bookingsMenu.setFont(menuFont);
        bookingsMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayBookingsForm();
            }
        });
        menuBar.add(bookingsMenu);

        // Customers Menu
        customersMenu = new JMenu("Customers");
        customersMenu.setForeground(Color.WHITE); // White text
        customersMenu.setFont(menuFont);
        if (isAdmin) {
            addAdminCustomerMenuItems(menuFont);
        } else {
            addUserCustomerMenuItems(menuFont);
        }
        menuBar.add(customersMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Adds menu items for admin customers.
     *
     * @param menuFont the font to be used for the menu items
     */
    private void addAdminCustomerMenuItems(Font menuFont) {
        JMenuItem viewActive = new JMenuItem("View Active Customers");
        viewActive.setToolTipText("Show only active (non-deleted) customers");
        viewActive.setIcon(loadScaledIcon("resources/icons/view.png", 18, 18));
        viewActive.setFont(menuFont);
        viewActive.addActionListener(e -> displayActiveCustomers());
        JMenuItem viewAll = new JMenuItem("View All Customers");
        viewAll.setToolTipText("Show all registered customers including deleted ones");
        viewAll.setIcon(loadScaledIcon("resources/icons/view_all.png", 18, 18));
        viewAll.setFont(menuFont);
        viewAll.addActionListener(e -> displayAllCustomers());
        JMenuItem addCustomer = new JMenuItem("Add New Customer");
        addCustomer.setToolTipText("Add a new customer");
        addCustomer.setIcon(loadScaledIcon("resources/icons/add.png", 18, 18));
        addCustomer.setFont(menuFont);
        addCustomer.addActionListener(e -> displayCustomersForm());
        JMenuItem updateCustomer = new JMenuItem("Update Customer");
        updateCustomer.setToolTipText("Update an existing customer");
        updateCustomer.setIcon(loadScaledIcon("resources/icons/update.png", 18, 18));
        updateCustomer.setFont(menuFont);
        updateCustomer.addActionListener(e -> displayCustomersForm());
        JMenuItem deleteCustomer = new JMenuItem("Delete Customer");
        deleteCustomer.setToolTipText("Delete (soft-delete) a customer");
        deleteCustomer.setIcon(loadScaledIcon("resources/icons/delete.png", 18, 18));
        deleteCustomer.setFont(menuFont);
        deleteCustomer.addActionListener(e -> displayCustomersForm());
        customersMenu.add(viewActive);
        customersMenu.add(viewAll);
        customersMenu.add(addCustomer);
        customersMenu.add(updateCustomer);
        customersMenu.add(deleteCustomer);
    }

    /**
     * Adds menu items for regular customers.
     *
     * @param menuFont the font to be used for the menu items
     */
    private void addUserCustomerMenuItems(Font menuFont) {
        JMenuItem myDetails = new JMenuItem("My Details");
        myDetails.setToolTipText("View your account details");
        myDetails.setIcon(loadScaledIcon("resources/icons/details.png", 24, 24));
        myDetails.setFont(menuFont);
        myDetails.addActionListener(e -> displayCustomerDetails(loggedInCustomerId));
        customersMenu.add(myDetails);
    }

    /**
     * Sets the admin mode for the window.
     *
     * @param isAdmin true if the user is an admin, false otherwise
     */
    public void setAdminMode(boolean isAdmin) {
        this.isAdmin = isAdmin;
        if (!isAdmin) {
            adminMenu.setVisible(false);
            flightsAdd.setEnabled(false);
            flightsDel.setEnabled(false);
            customersMenu.removeAll();
            addUserCustomerMenuItems(new Font("SansSerif", Font.BOLD, 18));
        } else {
            adminMenu.setVisible(true);
            flightsAdd.setEnabled(true);
            flightsDel.setEnabled(true);
            customersMenu.removeAll();
            addAdminCustomerMenuItems(new Font("SansSerif", Font.BOLD, 18));
        }
    }

    /**
     * Refreshes the table with the specified title.
     *
     * @param table the table to be refreshed
     * @param title the title of the table
     */
    private void refreshTable(JTable table, String title) {
        System.out.println("Refreshing table: " + title);
        getContentPane().removeAll();
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setTitle("Flight Booking System - " + title);
        revalidate();
        repaint();
        currentTable = table;
    }

    /**
     * Displays the upcoming flights in a table.
     */
    public void displayUpcomingFlights() {
        List<Flight> flights = fbs.getFlights();
        String[] columns = {"ID", "Flight Number", "Origin", "Destination", "Departure Date", "Base Price", "Capacity"};
        Object[][] data = new Object[flights.size()][7];
        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = flight.getBasePrice();
            data[i][6] = flight.getCapacity();
        }
        JTable table = new JTable(data, columns);
        refreshTable(table, "Upcoming Flights");
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int flightId = (int) table.getValueAt(row, 0);
                        System.out.println("Double-clicked flight ID: " + flightId);
                        displayFlightDetails(flightId);
                    } else {
                        System.out.println("No row selected on double-click.");
                    }
                }
            }
        });
    }

    /**
     * Displays all flights in a table.
     */
    public void displayAllFlights() {
        List<Flight> allFlights = new ArrayList<>(fbs.getAllFlights());
        String[] columns = {"ID", "Flight Number", "Origin", "Destination", "Departure Date", "Base Price", "Capacity"};
        Object[][] data = new Object[allFlights.size()][7];
        for (int i = 0; i < allFlights.size(); i++) {
            Flight flight = allFlights.get(i);
            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = flight.getBasePrice();
            data[i][6] = flight.getCapacity();
        }
        JTable table = new JTable(data, columns);
        refreshTable(table, "All Flights");
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int flightId = (int) table.getValueAt(row, 0);
                        System.out.println("Double-clicked flight ID: " + flightId);
                        displayFlightDetails(flightId);
                    } else {
                        System.out.println("No row selected on double-click.");
                    }
                }
            }
        });
    }

    /**
     * Displays detailed information about a flight.
     *
     * @param flightId the ID of the flight
     */
    public void displayFlightDetails(int flightId) {
        try {
            Flight flight = fbs.getFlightByID(flightId);
            StringBuilder details = new StringBuilder();
            details.append("Flight Details:\n")
                   .append("Flight Number: ").append(flight.getFlightNumber()).append("\n")
                   .append("Origin: ").append(flight.getOrigin()).append("\n")
                   .append("Destination: ").append(flight.getDestination()).append("\n")
                   .append("Departure Date: ").append(flight.getDepartureDate()).append("\n")
                   .append("Capacity: ").append(flight.getCapacity()).append("\n")
                   .append("Base Price: $").append(flight.getBasePrice()).append("\n\n")
                   .append("Passengers:\n");
            List<Customer> passengers = flight.getPassengers();
            if (passengers.isEmpty()) {
                details.append("No passengers have booked this flight.");
            } else {
                for (Customer c : passengers) {
                    details.append(c.getName()).append(" (").append(c.getPhone()).append(")\n");
                }
            }
            JOptionPane.showMessageDialog(this, details.toString(), "Flight Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving flight details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays the bookings in a table.
     */
    public void displayBookings() {
        List<Booking> bookingsList = fbs.getBookings();
        String[] columns = {"Booking ID", "Customer", "Flight", "Booking Date", "Fee"};
        Object[][] data = new Object[bookingsList.size()][5];
        for (int i = 0; i < bookingsList.size(); i++) {
            Booking booking = bookingsList.get(i);
            data[i][0] = booking.getId();
            data[i][1] = booking.getCustomer().getName();
            data[i][2] = booking.getFlight().getFlightNumber();
            data[i][3] = booking.getBookingDate();
            data[i][4] = booking.getBookingFee();
        }
        JTable table = new JTable(data, columns) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshTable(table, isAdmin ? "All Bookings" : "My Bookings");
    }

    /**
     * Displays all bookings (active and cancelled) in a table.
     */
    public void displayAllBookings() {
        List<Booking> active = fbs.getBookings();
        List<Booking> cancelled = fbs.getCancelledBookings();
        List<Booking> all = new ArrayList<>();
        all.addAll(active);
        all.addAll(cancelled);
        String[] columns = {"Booking ID", "Customer", "Flight", "Booking Date", "Fee", "Status"};
        Object[][] data = new Object[all.size()][6];
        for (int i = 0; i < all.size(); i++) {
            Booking booking = all.get(i);
            data[i][0] = booking.getId();
            data[i][1] = booking.getCustomer().getName();
            data[i][2] = booking.getFlight().getFlightNumber();
            data[i][3] = booking.getBookingDate();
            data[i][4] = booking.getBookingFee();
            data[i][5] = active.contains(booking) ? "Active" : "Cancelled";
        }
        JTable table = new JTable(data, columns) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshTable(table, isAdmin ? "All Bookings" : "My Bookings");
    }

    /**
     * Displays active customers in a table.
     */
    public void displayActiveCustomers() {
        List<Customer> active = fbs.getCustomers();
        String[] columns = {"ID", "Name", "Phone", "Email"};
        Object[][] data = new Object[active.size()][4];
        for (int i = 0; i < active.size(); i++) {
            Customer c = active.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.getEmail();
        }
        JTable table = new JTable(data, columns) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int customerId = (int) table.getValueAt(row, 0);
                        System.out.println("Double-clicked active customer ID: " + customerId);
                        showCustomerBookingDetails(customerId);
                    } else {
                        System.out.println("No row selected on double-click.");
                    }
                }
            }
        });
        refreshTable(table, "Active Customers");
    }

    /**
     * Displays all customers in a table.
     */
    public void displayAllCustomers() {
        List<Customer> all = fbs.getAllCustomers();
        String[] columns = {"ID", "Name", "Phone", "Email", "Active"};
        Object[][] data = new Object[all.size()][5];
        for (int i = 0; i < all.size(); i++) {
            Customer c = all.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.getEmail();
            data[i][4] = c.isDeleted() ? "No" : "Yes";
        }
        JTable table = new JTable(data, columns) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int customerId = (int) table.getValueAt(row, 0);
                        System.out.println("Double-clicked all customer ID: " + customerId);
                        showCustomerBookingDetails(customerId);
                    } else {
                        System.out.println("No row selected on double-click.");
                    }
                }
            }
        });
        refreshTable(table, "All Customers");
    }

    /**
     * Shows detailed booking information for a customer.
     *
     * @param customerId the ID of the customer
     */
    public void showCustomerBookingDetails(int customerId) {
        try {
            Customer customer = fbs.getCustomerByID(customerId);
            List<Booking> bookings = customer.getBookings();
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings found for " + customer.getName(),
                        "Customer Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder details = new StringBuilder();
            details.append("Booking Details for ").append(customer.getName()).append(":\n\n");
            for (Booking b : bookings) {
                Flight flight = b.getFlight();
                details.append("Flight Number: ").append(flight.getFlightNumber()).append("\n")
                       .append("Origin: ").append(flight.getOrigin()).append("\n")
                       .append("Destination: ").append(flight.getDestination()).append("\n")
                       .append("Departure Date: ").append(flight.getDepartureDate()).append("\n")
                       .append("Price: $").append(b.getBookingFee()).append("\n\n");
            }
            JOptionPane.showMessageDialog(this, details.toString(), "Customer Booking Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays the details of a customer.
     *
     * @param customerId the ID of the customer
     */
    public void displayCustomerDetails(int customerId) {
        try {
            Customer customer = fbs.getCustomerByID(customerId);
            String[] columns = {"ID", "Name", "Phone", "Email", "Bookings"};
            Object[][] data = new Object[1][5];
            data[0][0] = customer.getId();
            data[0][1] = customer.getName();
            data[0][2] = customer.getPhone();
            data[0][3] = customer.getEmail();
            data[0][4] = customer.getBookings().size();
            refreshTable(new JTable(data, columns), "My Details");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected flight.
     */
    private void deleteSelectedFlight() {
        if (currentTable != null) {
            int selectedRow = currentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int flightId = (int) currentTable.getValueAt(selectedRow, 0);
                try {
                    Flight flight = fbs.getFlightByID(flightId);
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Delete flight " + flight.getFlightNumber() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        fbs.removeFlight(flightId);
                        FlightBookingSystemData.store(fbs);
                        displayUpcomingFlights(); // Refresh the table after deletion
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting flight: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No flight selected for deletion.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Deletes the selected customer.
     */
    private void deleteSelectedCustomer() {
        if (currentTable != null) {
            int selectedRow = currentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int custId = (int) currentTable.getValueAt(selectedRow, 0);
                try {
                    fbs.removeCustomer(custId);
                    FlightBookingSystemData.store(fbs);
                    displayAllCustomers();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Exits the application, saving data before exit.
     */
    private void exitApplication() {
        try {
            FlightBookingSystemData.store(fbs);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            System.exit(0);
        }
    }

    /**
     * Returns the flight booking system instance.
     *
     * @return the flight booking system instance
     */
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    /**
     * Sets the logged-in customer ID.
     *
     * @param id the ID of the logged-in customer
     */
    public void setLoggedInCustomerId(int id) {
        this.loggedInCustomerId = id;
    }

    /**
     * Returns the logged-in customer ID.
     *
     * @return the ID of the logged-in customer
     */
    public Integer getLoggedInCustomerId() {
        return loggedInCustomerId;
    }

    /**
     * Handles action events for the menu items.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Not used directly; individual menu items have their own listeners.
    }

    private void createSidebarMenu() {
        List<JButton> buttons = new ArrayList<>();
        JButton viewCustomersButton = new JButton("View Customers");
        viewCustomersButton.setBackground(new Color(0x1976D2)); // Blue color
        viewCustomersButton.setForeground(Color.WHITE);
        viewCustomersButton.addActionListener(e -> displayAllCustomers());
        buttons.add(viewCustomersButton);

        JButton addBookingButton = new JButton("Add Booking");
        addBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        addBookingButton.setForeground(Color.WHITE);
        addBookingButton.addActionListener(e -> new AddBookingWindow(this));
        buttons.add(addBookingButton);

        JButton issueBookingButton = new JButton("Issue Booking");
        issueBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        issueBookingButton.setForeground(Color.WHITE);
        issueBookingButton.addActionListener(e -> new AddBookingWindow(this));
        buttons.add(issueBookingButton);

        JButton cancelBookingButton = new JButton("Cancel Booking");
        cancelBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        cancelBookingButton.setForeground(Color.WHITE);
        cancelBookingButton.addActionListener(e -> new CancelBookingWindow(this));
        buttons.add(cancelBookingButton);

        createSidebarMenu(buttons);
    }

    /**
     * Creates the sidebar menu with the specified buttons.
     *
     * @param buttons the list of buttons to be added to the sidebar
     */
    private void createSidebarMenu(List<JButton> buttons) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(buttons.size(), 1, 10, 10));
        sidebar.setBackground(new Color(0x1E3A5F)); // Dark blue background

        for (JButton button : buttons) {
            sidebar.add(button);
        }

        getContentPane().add(sidebar, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    /**
     * Returns the list of buttons for the flights menu.
     *
     * @return the list of buttons for the flights menu
     */
    private List<JButton> getFlightsButtons() {
        List<JButton> buttons = new ArrayList<>();

        JButton viewUpcomingButton = new JButton("View Upcoming Flights");
        viewUpcomingButton.setBackground(new Color(0x1976D2)); // Blue color
        viewUpcomingButton.setForeground(Color.WHITE);
        viewUpcomingButton.addActionListener(e -> displayUpcomingFlights());
        buttons.add(viewUpcomingButton);

        JButton viewAllButton = new JButton("View All Flights");
        viewAllButton.setBackground(new Color(0x1976D2)); // Blue color
        viewAllButton.setForeground(Color.WHITE);
        viewAllButton.addActionListener(e -> displayAllFlights());
        buttons.add(viewAllButton);

        JButton addFlightButton = new JButton("Add New Flight");
        addFlightButton.setBackground(new Color(0xFF9800)); // Orange color
        addFlightButton.setForeground(Color.WHITE);
        addFlightButton.addActionListener(e -> new AddFlightWindow(this));
        buttons.add(addFlightButton);

        JButton deleteFlightButton = new JButton("Delete Flight");
        deleteFlightButton.setBackground(new Color(0xFF9800)); // Orange color
        deleteFlightButton.setForeground(Color.WHITE);
        deleteFlightButton.addActionListener(e -> deleteSelectedFlight());
        buttons.add(deleteFlightButton);

        JButton filterFlightsButton = new JButton("Filter Flights");
        filterFlightsButton.setBackground(new Color(0x1976D2)); // Blue color
        filterFlightsButton.setForeground(Color.WHITE);
        filterFlightsButton.addActionListener(e -> new FilterFlightsWindow(fbs));
        buttons.add(filterFlightsButton);

        return buttons;
    }

    /**
     * Returns the list of buttons for the bookings menu.
     *
     * @return the list of buttons for the bookings menu
     */
    private List<JButton> getBookingsButtons() {
        List<JButton> buttons = new ArrayList<>();

        JButton viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.setBackground(new Color(0x1976D2)); // Blue color
        viewBookingsButton.setForeground(Color.WHITE);
        viewBookingsButton.addActionListener(e -> displayBookings());
        buttons.add(viewBookingsButton);

        JButton newBookingButton = new JButton("New Booking");
        newBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        newBookingButton.setForeground(Color.WHITE);
        newBookingButton.addActionListener(e -> new AddBookingWindow(this));
        buttons.add(newBookingButton);

        JButton updateBookingButton = new JButton("Update Booking");
        updateBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        updateBookingButton.setForeground(Color.WHITE);
        updateBookingButton.addActionListener(e -> new UpdateBookingWindow(this));
        buttons.add(updateBookingButton);

        JButton cancelBookingButton = new JButton("Cancel Booking");
        cancelBookingButton.setBackground(new Color(0xFF9800)); // Orange color
        cancelBookingButton.setForeground(Color.WHITE);
        cancelBookingButton.addActionListener(e -> new CancelBookingWindow(this));
        buttons.add(cancelBookingButton);

        JButton viewAllBookingsButton = new JButton("View All Bookings");
        viewAllBookingsButton.setBackground(new Color(0x1976D2)); // Blue color
        viewAllBookingsButton.setForeground(Color.WHITE);
        viewAllBookingsButton.addActionListener(e -> displayAllBookings());
        buttons.add(viewAllBookingsButton);

        return buttons;
    }

    /**
     * Returns the list of buttons for the customers menu.
     *
     * @return the list of buttons for the customers menu
     */
    private List<JButton> getCustomersButtons() {
        List<JButton> buttons = new ArrayList<>();

        JButton viewActiveCustomersButton = new JButton("View Active Customers");
        viewActiveCustomersButton.setBackground(new Color(0x1976D2)); // Blue color
        viewActiveCustomersButton.setForeground(Color.WHITE);
        viewActiveCustomersButton.addActionListener(e -> displayActiveCustomers());
        buttons.add(viewActiveCustomersButton);

        JButton viewAllCustomersButton = new JButton("View All Customers");
        viewAllCustomersButton.setBackground(new Color(0x1976D2)); // Blue color
        viewAllCustomersButton.setForeground(Color.WHITE);
        viewAllCustomersButton.addActionListener(e -> displayAllCustomers());
        buttons.add(viewAllCustomersButton);

        JButton addCustomerButton = new JButton("Add New Customer");
        addCustomerButton.setBackground(new Color(0xFF9800)); // Orange color
        addCustomerButton.setForeground(Color.WHITE);
        addCustomerButton.addActionListener(e -> new AddCustomerWindow(this));
        buttons.add(addCustomerButton);

        JButton updateCustomerButton = new JButton("Update Customer");
        updateCustomerButton.setBackground(new Color(0xFF9800)); // Orange color
        updateCustomerButton.setForeground(Color.WHITE);
        updateCustomerButton.addActionListener(e -> new UpdateCustomerWindow(this));
        buttons.add(updateCustomerButton);

        JButton deleteCustomerButton = new JButton("Delete Customer");
        deleteCustomerButton.setBackground(new Color(0xFF9800)); // Orange color
        deleteCustomerButton.setForeground(Color.WHITE);
        deleteCustomerButton.addActionListener(e -> deleteSelectedCustomer());
        buttons.add(deleteCustomerButton);

        return buttons;
    }

    /**
     * Displays the flights form.
     */
    private void displayFlightsForm() {
        // Clear the right side content
        getContentPane().removeAll();
        createSidebarMenu(getFlightsButtons());

        // Add the flights form or function
        JLabel flightsLabel = new JLabel("Flights Form", JLabel.CENTER);
        flightsLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        flightsLabel.setForeground(new Color(0x1E3A5F)); // Dark blue color
        getContentPane().add(flightsLabel, BorderLayout.NORTH);

        // Add common layout elements
        addCommonLayout();

        // Set the background color again
        getContentPane().setBackground(new Color(0xF5F5F5)); // Light gray background

        revalidate();
        repaint();
    }

    /**
     * Displays the bookings form.
     */
    private void displayBookingsForm() {
        // Clear the right side content
        getContentPane().removeAll();
        createSidebarMenu(getBookingsButtons());

        // Add the bookings form or function
        JLabel bookingsLabel = new JLabel("Bookings Form", JLabel.CENTER);
        bookingsLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        bookingsLabel.setForeground(new Color(0x1E3A5F)); // Dark blue color
        getContentPane().add(bookingsLabel, BorderLayout.NORTH);

        // Add common layout elements
        addCommonLayout();

        // Set the background color again
        getContentPane().setBackground(new Color(0xF5F5F5)); // Light gray background

        revalidate();
        repaint();
    }

    /**
     * Displays the customers form.
     */
    private void displayCustomersForm() {
        // Clear the right side content
        getContentPane().removeAll();
        createSidebarMenu(getCustomersButtons());

        // Add the customers form or function
        JLabel customersLabel = new JLabel("Customers Form", JLabel.CENTER);
        customersLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        customersLabel.setForeground(new Color(0x1E3A5F)); // Dark blue color
        getContentPane().add(customersLabel, BorderLayout.NORTH);

        // Add common layout elements
        addCommonLayout();

        // Set the background color again
        getContentPane().setBackground(new Color(0xF5F5F5)); // Light gray background

        revalidate();
        repaint();
    }

    /**
     * Adds common layout elements to the window.
     */
    private void addCommonLayout() {
        // Add image to the center at its original size
        ImageIcon originalIcon = new ImageIcon("resources/icons/main3.png");
        JLabel imageLabel = new JLabel(originalIcon);
        getContentPane().add(imageLabel, BorderLayout.CENTER);

        // Set the background color again
        getContentPane().setBackground(new Color(0xF5F5F5)); // Light gray background

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Displays the admin form.
     */
    private void displayAdminForm() {
        // Clear the right side content
        getContentPane().removeAll();
        createSidebarMenu(getAdminButtons());

        // Add the admin form or function
        JLabel adminLabel = new JLabel("Admin Form", JLabel.CENTER);
        adminLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        adminLabel.setForeground(new Color(0x1E3A5F)); // Dark blue color
        getContentPane().add(adminLabel, BorderLayout.NORTH);

        // Add common layout elements
        addCommonLayout();

        revalidate();
        repaint();
    }

    /**
     * Returns the list of buttons for the admin menu.
     *
     * @return the list of buttons for the admin menu
     */
    private List<JButton> getAdminButtons() {
        List<JButton> buttons = new ArrayList<>();

        JButton viewCustomersButton = new JButton("View Customers");
        viewCustomersButton.setBackground(new Color(0x1976D2)); // Blue color
        viewCustomersButton.setForeground(Color.WHITE);
        viewCustomersButton.addActionListener(e -> displayAllCustomers());
        buttons.add(viewCustomersButton);

        JButton addFlightButton = new JButton("Add New Flight");
        addFlightButton.setBackground(new Color(0xFF9800)); // Orange color
        addFlightButton.setForeground(Color.WHITE);
        addFlightButton.addActionListener(e -> new AddFlightWindow(this));
        buttons.add(addFlightButton);

        JButton deleteFlightButton = new JButton("Delete Flight");
        deleteFlightButton.setBackground(new Color(0xFF9800)); // Orange color
        deleteFlightButton.setForeground(Color.WHITE);
        deleteFlightButton.addActionListener(e -> deleteSelectedFlight());
        buttons.add(deleteFlightButton);

        return buttons;
    }

    /**
     * The main method to launch the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlightBookingSystem fbs = bcu.cmp5332.bookingsystem.data.FlightBookingSystemData.load();
                new LoginWindow(fbs);
            } catch (Exception ex) {
                System.err.println("Failed to initialize system: " + ex.getMessage());
            }
        });
    }
}