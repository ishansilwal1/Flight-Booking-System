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
    
    // UI Components that need to persist
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel headerLabel;
    
    // Track current active section
    private enum Section { HOME, FLIGHTS, BOOKINGS, CUSTOMERS, ADMIN }
    private Section currentSection = Section.HOME;

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
     * Gets the project root directory by searching for the resources folder.
     * @return the absolute path to the project root
     */
    private String getProjectRoot() {
        // Get the location of the class file and navigate to project root
        String classPath = MainWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // Handle Windows paths (remove leading slash if present)
        if (classPath.startsWith("/") && classPath.contains(":")) {
            classPath = classPath.substring(1);
        }
        // Decode URL encoding (e.g., %20 for spaces)
        try {
            classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
        } catch (Exception e) {
            // Ignore decoding errors
        }
        File classDir = new File(classPath);
        // Navigate up to find project root (look for resources folder)
        File current = classDir.isDirectory() ? classDir : classDir.getParentFile();
        while (current != null) {
            File resourcesDir = new File(current, "resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                return current.getAbsolutePath().replace("\\", "/");
            }
            current = current.getParentFile();
        }
        // Fallback to current working directory
        return System.getProperty("user.dir").replace("\\", "/");
    }

    /**
     * Loads and scales an icon from the specified file path.
     *
     * @param path the relative file path of the icon (e.g., "resources/icons/view.png")
     * @param width the desired width of the icon
     * @param height the desired height of the icon
     * @return the scaled ImageIcon
     */
    private ImageIcon loadScaledIcon(String path, int width, int height) {
        String absolutePath = getProjectRoot() + "/" + path;
        File f = new File(absolutePath);
        if (!f.exists()) {
            System.err.println("Icon file not found: " + absolutePath);
        }
        ImageIcon icon = new ImageIcon(absolutePath);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Flight Booking Management System");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0xF5F5F5));
        
        // Initialize persistent UI components
        initializeMainLayout();
        initMenuBar();
        
        // Show welcome/home screen
        showHomeScreen();
    }
    
    /**
     * Initializes the main layout with sidebar and content area.
     */
    private void initializeMainLayout() {
        // Create sidebar panel
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(0x1E3A5F));
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(0xF5F5F5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Create header label
        headerLabel = new JLabel("Welcome to ISAM Airlines", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0x1E3A5F));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        getContentPane().add(headerLabel, BorderLayout.NORTH);
    }
    
    /**
     * Shows the home/welcome screen.
     */
    private void showHomeScreen() {
        currentSection = Section.HOME;
        setHeader("Welcome to ISAM Airlines");
        updateSidebar(getHomeSidebarButtons());
        showWelcomeImage();
    }
    
    /**
     * Updates the header label text.
     */
    private void setHeader(String text) {
        headerLabel.setText(text);
    }
    
    /**
     * Updates the sidebar with the given buttons.
     */
    private void updateSidebar(List<JButton> buttons) {
        sidebarPanel.removeAll();
        sidebarPanel.setLayout(new GridLayout(Math.max(buttons.size(), 6), 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        for (JButton button : buttons) {
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            sidebarPanel.add(button);
        }
        
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }
    
    /**
     * Updates the content panel with the given component.
     */
    private void updateContent(Component component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the welcome image in the content area.
     */
    private void showWelcomeImage() {
        String imagePath = getProjectRoot() + "/resources/icons/main3.png";
        ImageIcon originalIcon = new ImageIcon(imagePath);
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        updateContent(imageLabel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Gets the sidebar buttons for the home screen.
     */
    private List<JButton> getHomeSidebarButtons() {
        List<JButton> buttons = new ArrayList<>();
        
        if (isAdmin) {
            buttons.add(createSidebarButton("View All Flights", new Color(0x1976D2), e -> displayAllFlights()));
            buttons.add(createSidebarButton("View All Customers", new Color(0x1976D2), e -> displayAllCustomers()));
            buttons.add(createSidebarButton("View All Bookings", new Color(0x1976D2), e -> displayAllBookings()));
            buttons.add(createSidebarButton("Add Flight", new Color(0xFF9800), e -> new AddFlightWindow(this)));
            buttons.add(createSidebarButton("Add Customer", new Color(0xFF9800), e -> new AddCustomerWindow(this)));
            buttons.add(createSidebarButton("New Booking", new Color(0xFF9800), e -> new AddBookingWindow(this)));
        } else {
            buttons.add(createSidebarButton("View Flights", new Color(0x1976D2), e -> displayUpcomingFlights()));
            buttons.add(createSidebarButton("My Bookings", new Color(0x1976D2), e -> displayMyBookings()));
            buttons.add(createSidebarButton("New Booking", new Color(0xFF9800), e -> new AddBookingWindow(this)));
            buttons.add(createSidebarButton("Cancel Booking", new Color(0xFF9800), e -> new CancelBookingWindow(this)));
        }
        
        return buttons;
    }
    
    /**
     * Helper method to create a styled sidebar button.
     */
    private JButton createSidebarButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.addActionListener(action);
        return button;
    }

    /**
     * Adds a welcome message to the window.
     */
    private void addWelcomeMessage() {
        // Now handled by headerLabel
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
        // Refresh the sidebar to show appropriate buttons for the user role
        updateSidebar(getHomeSidebarButtons());
    }

    /**
     * Refreshes the table with the specified title.
     *
     * @param table the table to be refreshed
     * @param title the title of the table
     */
    private void refreshTable(JTable table, String title) {
        System.out.println("Refreshing table: " + title);
        setHeader(title);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateContent(scrollPane);
        setTitle("Flight Booking System - " + title);
        currentTable = table;
    }
    
    /**
     * Displays bookings for the logged-in customer.
     */
    public void displayMyBookings() {
        if (loggedInCustomerId == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your bookings.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Customer customer = fbs.getCustomerByID(loggedInCustomerId);
            List<Booking> myBookings = customer.getBookings();
            String[] columns = {"Booking ID", "Flight", "Origin", "Destination", "Date", "Fee"};
            Object[][] data = new Object[myBookings.size()][6];
            for (int i = 0; i < myBookings.size(); i++) {
                Booking booking = myBookings.get(i);
                Flight flight = booking.getFlight();
                data[i][0] = booking.getId();
                data[i][1] = flight.getFlightNumber();
                data[i][2] = flight.getOrigin();
                data[i][3] = flight.getDestination();
                data[i][4] = booking.getBookingDate();
                data[i][5] = String.format("$%.2f", booking.getBookingFee());
            }
            JTable table = new JTable(data, columns) {
                private static final long serialVersionUID = 1L;
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            refreshTable(table, "My Bookings");
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    /**
     * Returns the list of buttons for the flights menu.
     *
     * @return the list of buttons for the flights menu
     */
    private List<JButton> getFlightsButtons() {
        List<JButton> buttons = new ArrayList<>();
        
        buttons.add(createSidebarButton("View Upcoming", new Color(0x1976D2), e -> displayUpcomingFlights()));
        buttons.add(createSidebarButton("View All Flights", new Color(0x1976D2), e -> displayAllFlights()));
        buttons.add(createSidebarButton("Filter Flights", new Color(0x1976D2), e -> new FilterFlightsWindow(fbs)));
        
        if (isAdmin) {
            buttons.add(createSidebarButton("Add Flight", new Color(0xFF9800), e -> new AddFlightWindow(this)));
            buttons.add(createSidebarButton("Delete Flight", new Color(0xFF9800), e -> deleteSelectedFlight()));
        }
        
        buttons.add(createSidebarButton("← Back to Home", new Color(0x607D8B), e -> showHomeScreen()));

        return buttons;
    }

    /**
     * Returns the list of buttons for the bookings menu.
     *
     * @return the list of buttons for the bookings menu
     */
    private List<JButton> getBookingsButtons() {
        List<JButton> buttons = new ArrayList<>();

        if (isAdmin) {
            buttons.add(createSidebarButton("View All Bookings", new Color(0x1976D2), e -> displayAllBookings()));
            buttons.add(createSidebarButton("Active Bookings", new Color(0x1976D2), e -> displayBookings()));
        } else {
            buttons.add(createSidebarButton("My Bookings", new Color(0x1976D2), e -> displayMyBookings()));
        }
        
        buttons.add(createSidebarButton("New Booking", new Color(0xFF9800), e -> new AddBookingWindow(this)));
        buttons.add(createSidebarButton("Update Booking", new Color(0xFF9800), e -> new UpdateBookingWindow(this)));
        buttons.add(createSidebarButton("Cancel Booking", new Color(0xFF9800), e -> new CancelBookingWindow(this)));
        buttons.add(createSidebarButton("← Back to Home", new Color(0x607D8B), e -> showHomeScreen()));

        return buttons;
    }

    /**
     * Returns the list of buttons for the customers menu.
     *
     * @return the list of buttons for the customers menu
     */
    private List<JButton> getCustomersButtons() {
        List<JButton> buttons = new ArrayList<>();

        if (isAdmin) {
            buttons.add(createSidebarButton("Active Customers", new Color(0x1976D2), e -> displayActiveCustomers()));
            buttons.add(createSidebarButton("All Customers", new Color(0x1976D2), e -> displayAllCustomers()));
            buttons.add(createSidebarButton("Add Customer", new Color(0xFF9800), e -> new AddCustomerWindow(this)));
            buttons.add(createSidebarButton("Update Customer", new Color(0xFF9800), e -> new UpdateCustomerWindow(this)));
            buttons.add(createSidebarButton("Delete Customer", new Color(0xFF9800), e -> deleteSelectedCustomer()));
        } else {
            buttons.add(createSidebarButton("My Details", new Color(0x1976D2), e -> displayCustomerDetails(loggedInCustomerId)));
        }
        
        buttons.add(createSidebarButton("← Back to Home", new Color(0x607D8B), e -> showHomeScreen()));

        return buttons;
    }

    /**
     * Displays the flights form.
     */
    private void displayFlightsForm() {
        currentSection = Section.FLIGHTS;
        setHeader("Flights Management");
        updateSidebar(getFlightsButtons());
        showWelcomeImage();
    }

    /**
     * Displays the bookings form.
     */
    private void displayBookingsForm() {
        currentSection = Section.BOOKINGS;
        setHeader("Bookings Management");
        updateSidebar(getBookingsButtons());
        showWelcomeImage();
    }

    /**
     * Displays the customers form.
     */
    private void displayCustomersForm() {
        currentSection = Section.CUSTOMERS;
        setHeader("Customers Management");
        updateSidebar(getCustomersButtons());
        showWelcomeImage();
    }

    /**
     * Adds common layout elements to the window.
     */
    private void addCommonLayout() {
        // This method is now replaced by showWelcomeImage()
        showWelcomeImage();
    }

    /**
     * Displays the admin form.
     */
    private void displayAdminForm() {
        currentSection = Section.ADMIN;
        setHeader("Admin Dashboard");
        updateSidebar(getAdminButtons());
        showWelcomeImage();
    }

    /**
     * Returns the list of buttons for the admin menu.
     *
     * @return the list of buttons for the admin menu
     */
    private List<JButton> getAdminButtons() {
        List<JButton> buttons = new ArrayList<>();

        buttons.add(createSidebarButton("All Flights", new Color(0x1976D2), e -> displayAllFlights()));
        buttons.add(createSidebarButton("All Customers", new Color(0x1976D2), e -> displayAllCustomers()));
        buttons.add(createSidebarButton("All Bookings", new Color(0x1976D2), e -> displayAllBookings()));
        buttons.add(createSidebarButton("Add Flight", new Color(0xFF9800), e -> new AddFlightWindow(this)));
        buttons.add(createSidebarButton("Add Customer", new Color(0xFF9800), e -> new AddCustomerWindow(this)));
        buttons.add(createSidebarButton("Delete Flight", new Color(0xFF9800), e -> deleteSelectedFlight()));
        buttons.add(createSidebarButton("← Back to Home", new Color(0x607D8B), e -> showHomeScreen()));

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