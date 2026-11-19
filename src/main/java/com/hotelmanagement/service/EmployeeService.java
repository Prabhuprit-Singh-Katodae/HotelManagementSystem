package com.hotelmanagement.service;

import com.hotelmanagement.dao.EmployeeDAO;
import com.hotelmanagement.entity.Employee;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.math.BigDecimal;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<Employee> search(String q) {
        return HibernateUtil.read(s -> employeeDAO.search(s, q));
    }

    public Employee get(Integer id) {
        Employee e = HibernateUtil.read(s -> employeeDAO.findById(s, id));
        if (e == null) throw new ServiceException("Employee not found.");
        return e;
    }

    public void add(String name, String role, String phone, BigDecimal salary) {
        String n = Validator.required(name, "Name", 100);
        String r = Validator.required(role, "Role", 50);
        String p = Validator.phone(phone, false);
        BigDecimal sal = Validator.money(salary, "Salary", true);
        HibernateUtil.txRun(s -> {
            Employee e = new Employee();
            e.setName(n);
            e.setRole(r);
            e.setPhone(p);
            e.setSalary(sal.doubleValue());
            employeeDAO.save(s, e);
        });
    }

    public void update(Integer id, String name, String role, String phone, BigDecimal salary) {
        String n = Validator.required(name, "Name", 100);
        String r = Validator.required(role, "Role", 50);
        String p = Validator.phone(phone, false);
        BigDecimal sal = Validator.money(salary, "Salary", true);
        HibernateUtil.txRun(s -> {
            Employee e = employeeDAO.findById(s, id);
            if (e == null) throw new ServiceException("Employee not found.");
            e.setName(n);
            e.setRole(r);
            e.setPhone(p);
            e.setSalary(sal.doubleValue());
        });
    }

    public void delete(Integer id) {
        HibernateUtil.txRun(s -> {
            Employee e = employeeDAO.findById(s, id);
            if (e == null) throw new ServiceException("Employee not found.");
            employeeDAO.delete(s, e);
        });
    }
}
