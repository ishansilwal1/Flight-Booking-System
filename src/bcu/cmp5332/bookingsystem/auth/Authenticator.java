package bcu.cmp5332.bookingsystem.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * The Authenticator class handles user authentication for the flight booking system.
 * It manages a list of users and provides methods for logging in.
 */
public class Authenticator {
    private List<User> users = new ArrayList<>();
    
    /**
     * Constructs an Authenticator and pre-populates it with demo users.
     */
    public Authenticator() {
        // Pre-populate with demo users
        users.add(new User("admin", "admin123", Role.ADMIN));
        users.add(new User("customer", "cust123", Role.CUSTOMER));
    }
    
    /**
     * Attempts to log in a user with the specified username and password.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return the User object if the login is successful, null otherwise
     */
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }
}