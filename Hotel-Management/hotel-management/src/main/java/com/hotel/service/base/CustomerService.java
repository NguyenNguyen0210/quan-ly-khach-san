package com.hotel.service.base;

import com.hotel.entity.Customer;
import java.util.List;

public interface CustomerService {

    void registerCustomer(Customer customer);

    Customer getCustomerById(Long id);

    List<Customer> getAllCustomers();

    Customer findByPhone(String phone);

    Customer findByEmail(String email);

    Customer findByIdCard(String idCard);

    void updateCustomer(Customer customer);

    void deleteCustomer(Long customerId);
}
