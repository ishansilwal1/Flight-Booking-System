package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * This class represents the login window for the flight booking system.
 * It allows users to log in as either an admin or a customer.
 */
public class LoginWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private FlightBookingSystem fbs;
    private JTextField usernameField = new JTextField(20); // Increased size
    private JPasswordField passwordField = new JPasswordField(20); // Increased size
    private JButton loginButton = new JButton("Login");
    private JLabel messageLabel = new JLabel();

    /**
     * Constructs a new LoginWindow.
     *
     * @param fbs the flight booking system instance
     */
    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    /**
     * Initializes the components and layout of the window.
     */
    private void initialize() {
        setTitle("Flight Booking System - Login");
        setSize(800, 400);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(0xF5F5F5));

        // Navigation bar
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(0x1E3A5F));
        navBar.setPreferredSize(new Dimension(getWidth(), 50));
        JLabel navTitle = new JLabel("Welcome to ISAM Airlines");
        navTitle.setForeground(Color.WHITE);
        navTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        navBar.add(navTitle);
        add(navBar, BorderLayout.NORTH);

        // Left panel with image and gradient background
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(0x1E3A5F);
                Color color2 = new Color(0x3A607F);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        leftPanel.setLayout(new BorderLayout());
        File imageFile = new File("resources/icons/Login.png");
        if (imageFile.exists()) {
            ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(300, 400, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            leftPanel.add(imageLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Image not found");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leftPanel.add(errorLabel, BorderLayout.CENTER);
        }
        leftPanel.setPreferredSize(new Dimension(300, 0)); // Adjust the width of the left panel
        add(leftPanel, BorderLayout.WEST);

        // Right panel with form
        JPanel rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(0xF5F7FA);
                Color color2 = new Color(0xFFFFFF);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        rightPanel.setBackground(new Color(0xF5F5F5));
        JLabel headingLabel = new JLabel("Enter your credentials", JLabel.CENTER);
        headingLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        headingLabel.setForeground(new Color(0x1E3A5F));
        rightPanel.add(headingLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(0xF5F5F5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField.setFont(new Font("Roboto", Font.PLAIN, 16));
        usernameField.setBorder(new LineBorder(Color.GRAY, 1, true));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                usernameField.setBorder(new LineBorder(new Color(0x1E88E5), 1, true));
            }

            @Override
            public void focusLost(FocusEvent e) {
                usernameField.setBorder(new LineBorder(Color.GRAY, 1, true));
            }
        });
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        passwordField.setBorder(new LineBorder(Color.GRAY, 1, true));
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(new LineBorder(new Color(0x1E88E5), 1, true));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(new LineBorder(Color.GRAY, 1, true));
            }
        });
        inputPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.setBackground(new Color(0xFF9800));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Poppins", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(160, 45)); // Increased size
        loginButton.setBorder(new LineBorder(new Color(0xFF9800), 1, true));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFF9800), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        inputPanel.add(loginButton, gbc);
        loginButton.addActionListener(this);

        rightPanel.add(inputPanel, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Handles action events for the login button.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Admin login (hard-coded)
        if (username.equals("admin") && password.equals("admin")) {
            this.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                MainWindow mainWindow = new MainWindow(fbs);
                mainWindow.setAdminMode(true);
            });
        } else {
            // Customer login: username is name, password is stored password.
            Customer customer = authenticateCustomer(username, password);
            if (customer != null) {
                this.setVisible(false);
                SwingUtilities.invokeLater(() -> {
                    MainWindow mainWindow = new MainWindow(fbs);
                    mainWindow.setAdminMode(false);
                    mainWindow.setLoggedInCustomerId(customer.getId());
                    mainWindow.displayCustomerDetails(customer.getId());
                });
            } else {
                messageLabel.setText("Invalid credentials. Try again.");
                messageLabel.setForeground(Color.RED);
            }
        }
    }

    /**
     * Authenticates the customer based on the provided username and password.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return the authenticated customer, or null if authentication fails
     */
    private Customer authenticateCustomer(String username, String password) {
        for (Customer customer : fbs.getCustomers()) {
            if (customer.getName().equalsIgnoreCase(username) && customer.getPassword().equals(password)) {
                return customer;
            }
        }
        return null;
    }
}