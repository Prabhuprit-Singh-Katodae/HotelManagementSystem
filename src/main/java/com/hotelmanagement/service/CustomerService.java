package com.hotelmanagement.service;

import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.entity.Customer;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> search(String q) {
        return HibernateUtil.read(s -> customerDAO.search(s, q));
    }

    public Customer get(Integer id) {
        Customer c = HibernateUtil.read(s -> customerDAO.findById(s, id));
        if (c == null) throw new ServiceException("Customer not found.");
        return c;
    }

    public Integer add(String name, String phone, String email, String address) {
        String n = Validator.required(name, "Name", 100);
        String ph = Validator.phone(phone, true);
        String em = Validator.email(email, false);
        String ad = Validator.optional(address, "Address", 255);
        return HibernateUtil.tx(s -> {
            if (em != null && customerDAO.findByEmail(s, em) != null) {
                throw new ServiceException("A customer with this email already exists.");
            }
            Customer c = new Customer();
            c.setName(n);
            c.setPhone(ph);
            c.setEmail(em);
            c.setAddress(ad);
            customerDAO.save(s, c);
            return c.getCustomerId();
        });
    }

    public void update(Integer id, String name, String phone, String email, String address) {
        String n = Validator.required(name, "Name", 100);
        String ph = Validator.phone(phone, true);
        String em = Validator.email(email, false);
        String ad = Validator.optional(address, "Address", 255);
        HibernateUtil.txRun(s -> {
            Customer c = customerDAO.findById(s, id);
            if (c == null) throw new ServiceException("Customer not found.");
            Customer other = em == null ? null : customerDAO.findByEmail(s, em);
            if (other != null && !other.getCustomerId().equals(id)) {
                throw new ServiceException("Another customer already uses this email.");
            }
            c.setName(n);
            c.setPhone(ph);
            c.setEmail(em);
            c.setAddress(ad);
        });
    }

    public void delete(Integer id) {
        HibernateUtil.txRun(s -> {
            Customer c = customerDAO.findById(s, id);
            if (c == null) throw new ServiceException("Customer not found.");
            customerDAO.delete(s, c);
        });
    }
}
