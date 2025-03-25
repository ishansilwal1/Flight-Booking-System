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
    private final String RESOURCE = "./resources/data/customers.txt";
    
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