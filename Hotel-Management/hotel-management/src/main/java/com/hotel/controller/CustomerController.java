package com.hotel.controller;

import com.hotel.entity.Customer;
import com.hotel.service.base.CustomerService;
import com.hotel.service.base.CustomerServiceImpl;

import java.util.List;
import java.util.Objects;

public class CustomerController {

    private final CustomerService customerService = new CustomerServiceImpl();

    public void registerCustomer(Customer customer) {
        customerService.registerCustomer(customer);
    }

    public Customer getCustomerById(Long customerId) {
        if (customerId == null) {
            return null;
        }
        return customerService.getCustomerById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Customer findCustomerByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return customerService.findByPhone(phone);
    }

    public Customer findCustomerByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return customerService.findByEmail(email);
    }

    public Customer findCustomerByIdCard(String idCard) {
        if (idCard == null || idCard.isBlank()) {
            return null;
        }
        return customerService.findByIdCard(idCard);
    }

    public void updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
    }

    public void deleteCustomer(Long customerId) {
        customerService.deleteCustomer(customerId);
    }
}
