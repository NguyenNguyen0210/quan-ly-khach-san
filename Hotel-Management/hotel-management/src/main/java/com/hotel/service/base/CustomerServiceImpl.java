package com.hotel.service.base;

import com.hotel.dao.CustomerDAO;
import com.hotel.entity.Customer;

import java.util.List;
import java.util.Objects;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void registerCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer must not be null");
        Objects.requireNonNull(customer.getEmail(), "Customer email is required");
        Objects.requireNonNull(customer.getPhone(), "Customer phone is required");
        Objects.requireNonNull(customer.getIdCard(), "Customer id card is required");

        if (customerDAO.findByEmail(customer.getEmail()) != null) {
            throw new IllegalStateException("Email is already registered.");
        }
        if (customerDAO.findByPhone(customer.getPhone()) != null) {
            throw new IllegalStateException("Phone number is already registered.");
        }
        if (customerDAO.findByIdCard(customer.getIdCard()) != null) {
            throw new IllegalStateException("ID card is already registered.");
        }

        customerDAO.save(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        if (id == null) {
            return null;
        }
        return customerDAO.findById(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    @Override
    public Customer findByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return customerDAO.findByPhone(phone);
    }

    @Override
    public Customer findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return customerDAO.findByEmail(email);
    }

    @Override
    public Customer findByIdCard(String idCard) {
        if (idCard == null || idCard.isBlank()) {
            return null;
        }
        return customerDAO.findByIdCard(idCard);
    }

    @Override
    public void updateCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer must not be null");
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer id is required for update.");
        }
        customerDAO.update(customer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer id is required for delete.");
        }
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            return;
        }
        customerDAO.delete(customer);
    }
}
