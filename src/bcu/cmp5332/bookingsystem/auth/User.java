package bcu.cmp5332.bookingsystem.auth;

/**
 * The User class represents a user in the flight booking system.
 * It contains the user's username, password, and role.
 */
public class User {
    private String username;
    private String password; // For simplicity, we store plaintext (in a real system use hashing)
    private Role role;
    
    /**
     * Constructs a new User with the specified username, password, and role.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param role     the role of the user
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the role of the user.
     *
     * @return the role of the user
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * Checks if the provided password matches the user's password.
     *
     * @param input the password to check
     * @return true if the password matches, false otherwise
     */
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }
}