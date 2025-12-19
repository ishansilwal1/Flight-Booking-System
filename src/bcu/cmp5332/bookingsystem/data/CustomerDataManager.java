package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CustomerDataManager implements DataManager {
    private final String RESOURCE;
    
    public CustomerDataManager() {
        // Get the absolute path relative to the project root
        String projectRoot = getProjectRoot();
        RESOURCE = projectRoot + "/resources/data/customers.txt";
    }
    
    private String getProjectRoot() {
        // Get the location of the class file and navigate to project root
        String classPath = CustomerDataManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
        java.io.File classDir = new java.io.File(classPath);
        // Navigate up to find project root (look for resources folder)
        java.io.File current = classDir.isDirectory() ? classDir : classDir.getParentFile();
        while (current != null) {
            java.io.File resourcesDir = new java.io.File(current, "resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                return current.getAbsolutePath().replace("\\", "/");
            }
            current = current.getParentFile();
        }
        // Fallback to current working directory
        return System.getProperty("user.dir").replace("\\", "/");
    }
    
    /**
     * Loads customer data from a file and adds it to the FlightBookingSystem.
     *
     * @param fbs the FlightBookingSystem to which the customer data will be added
     * @throws IOException if an I/O error occurs
     * @throws FlightBookingSystemException if there is an error parsing the customer data
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(RESOURCE);
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(file)) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(line.isEmpty()){
                    line_idx++;
                    continue;
                }
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    String password = (properties.length > 4 && !properties[4].isEmpty()) ? properties[4] : "default123";
                    boolean isDeleted = properties.length > 5 && !properties[5].isEmpty() ? Boolean.parseBoolean(properties[5]) : false;
                    
                    Customer customer = new Customer(id, name, phone, email, password);
                    customer.setDeleted(isDeleted);
                    fbs.addCustomer(customer);
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer data on line " + line_idx + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }
    
    /**
     * Stores customer data from the FlightBookingSystem to a file.
     *
     * @param fbs the FlightBookingSystem from which the customer data will be retrieved
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getAllCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                out.print(customer.getPassword() + SEPARATOR);
                out.print(customer.isDeleted() + SEPARATOR);
                out.println();
            }
        }
    }
}