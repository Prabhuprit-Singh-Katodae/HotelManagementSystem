package com.hotelmanagement.service;

import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.dao.EmployeeDAO;
import com.hotelmanagement.dao.UserAccountDAO;
import com.hotelmanagement.entity.UserAccount;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.PasswordUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.util.List;

/** Admin tools for user accounts (create, activate/deactivate, change role, reset password). */
public class UserService {

    public static final List<String> ROLES = List.of(UserAccount.ROLE_CUSTOMER, UserAccount.ROLE_STAFF, UserAccount.ROLE_ADMIN);

    private final UserAccountDAO userDAO = new UserAccountDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<UserAccount> list() {
        return HibernateUtil.read(s -> userDAO.findAllByUsername(s));
    }

    private static String normalizeRole(String role) {
        for (String r : ROLES) {
            if (r.equalsIgnoreCase(role == null ? "" : role.trim())) return r;
        }
        throw new ServiceException("Invalid role.");
    }

    public void create(String username, String password, String role, Integer employeeId, Integer customerId) {
        String un = Validator.username(username);
        Validator.password(password);
        String r = normalizeRole(role);
        HibernateUtil.txRun(s -> {
            if (userDAO.findByUsername(s, un) != null) throw new ServiceException("That username is already taken.");
            UserAccount u = new UserAccount();
            u.setUsername(un);
            u.setPasswordHash(PasswordUtil.hash(password));
            u.setRole(r);
            u.setStatus(UserAccount.ACTIVE);
            if (UserAccount.ROLE_CUSTOMER.equals(r)) {
                if (customerId == null || customerDAO.findById(s, customerId) == null) {
                    throw new ServiceException("A CUSTOMER account must be linked to an existing customer.");
                }
                if (userDAO.findByCustomerId(s, customerId) != null) {
                    throw new ServiceException("This customer already has a login account.");
                }
                u.setCustomer(customerDAO.findById(s, customerId));
            } else if (employeeId != null) {
                if (employeeDAO.findById(s, employeeId) == null) throw new ServiceException("Employee not found.");
                u.setEmployee(employeeDAO.findById(s, employeeId));
            }
            userDAO.save(s, u);
        });
    }

    public void setStatus(Integer userId, String status, LoginUser actor) {
        final String st = UserAccount.ACTIVE.equalsIgnoreCase(status) ? UserAccount.ACTIVE : UserAccount.INACTIVE;
        HibernateUtil.txRun(s -> {
            UserAccount u = userDAO.findById(s, userId);
            if (u == null) throw new ServiceException("User not found.");
            if (u.getUserId().equals(actor.getUserId()) && UserAccount.INACTIVE.equals(st)) {
                throw new ServiceException("You cannot deactivate your own account.");
            }
            u.setStatus(st);
        });
    }

    public void setRole(Integer userId, String role, LoginUser actor) {
        final String r = normalizeRole(role);
        HibernateUtil.txRun(s -> {
            UserAccount u = userDAO.findById(s, userId);
            if (u == null) throw new ServiceException("User not found.");
            if (u.getUserId().equals(actor.getUserId()) && !r.equals(u.getRole())) {
                throw new ServiceException("You cannot change your own role.");
            }
            if (UserAccount.ROLE_CUSTOMER.equals(r) && u.getCustomer() == null) {
                throw new ServiceException("Only accounts linked to a customer can have the CUSTOMER role.");
            }
            u.setRole(r);
        });
    }

    public void resetPassword(Integer userId, String newPassword) {
        Validator.password(newPassword);
        HibernateUtil.txRun(s -> {
            UserAccount u = userDAO.findById(s, userId);
            if (u == null) throw new ServiceException("User not found.");
            u.setPasswordHash(PasswordUtil.hash(newPassword));
        });
    }
}
