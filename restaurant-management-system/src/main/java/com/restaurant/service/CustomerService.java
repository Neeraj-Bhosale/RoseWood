package com.restaurant.service;

import com.restaurant.data.DataStructureManager;
import com.restaurant.model.Customer;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.util.ValidationUtil;

import java.util.List;
import java.util.logging.Logger;

public class CustomerService {
    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepository();
    }

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = customerRepository.findAll();
        for (Customer c : list) {
            DataStructureManager.getInstance().registerCustomerInIndex(c);
        }
        return list;
    }

    public Customer getCustomerById(int customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer getCustomerByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        Customer cached = DataStructureManager.getInstance().findCustomerByPhone(phone);
        if (cached != null) return cached;
        Customer fromDb = customerRepository.findByPhone(phone.trim());
        if (fromDb != null) {
            DataStructureManager.getInstance().registerCustomerInIndex(fromDb);
        }
        return fromDb;
    }

    public boolean registerCustomer(String name, String phone, String email, String address) {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Valid phone number is required.");
        }
        if (ValidationUtil.isNotEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        Customer existing = customerRepository.findByPhone(phone.trim());
        if (existing != null) {
            throw new IllegalArgumentException("A customer with phone " + phone + " already exists.");
        }

        Customer customer = new Customer(0, name.trim(), phone.trim(), email != null ? email.trim() : "", address != null ? address.trim() : "");
        boolean created = customerRepository.create(customer);
        if (created) {
            DataStructureManager.getInstance().registerCustomerInIndex(customer);
            LOGGER.info("New customer registered: " + customer.getName());
        }
        return created;
    }

    public boolean updateCustomer(Customer customer) {
        if (customer == null || customer.getCustomerId() <= 0) return false;
        if (!ValidationUtil.isNotEmpty(customer.getName())) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        return customerRepository.update(customer);
    }

    public boolean deleteCustomer(int customerId) {
        return customerRepository.delete(customerId);
    }
}
