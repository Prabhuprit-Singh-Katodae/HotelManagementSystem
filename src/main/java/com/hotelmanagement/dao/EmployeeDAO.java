package com.hotelmanagement.dao;

import com.hotelmanagement.entity.Employee;
import java.util.List;
import org.hibernate.Session;

public class EmployeeDAO extends BaseDAO<Employee> {

    public EmployeeDAO() {
        super(Employee.class, "employeeId", true);
    }

    public List<Employee> search(Session session, String q) {
        if (q == null || q.isBlank()) return findAll(session);
        return session.createQuery(
                "from Employee e where lower(e.name) like :q or lower(e.role) like :q or e.phone like :q order by e.employeeId",
                Employee.class).setParameter("q", "%" + q.trim().toLowerCase() + "%").getResultList();
    }
}
