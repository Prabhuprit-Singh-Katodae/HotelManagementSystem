package com.hotelmanagement.service;

import com.hotelmanagement.dao.CustomerDAO;
import com.hotelmanagement.dao.UserAccountDAO;
import com.hotelmanagement.entity.Customer;
import com.hotelmanagement.entity.UserAccount;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.LoginUser;
import com.hotelmanagement.util.PasswordUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;

/** Registration, login and password handling. */
public class AuthService {

    private final UserAccountDAO userDAO = new UserAccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    /** Checks username + password and returns the data to keep in the session. */
    public LoginUser login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new ServiceException("Please enter your username and password.");
        }
        return HibernateUtil.read(session -> {
            UserAccount u = userDAO.findByUsername(session, username.trim());
            if (u == null || !PasswordUtil.matches(password, u.getPasswordHash())) {
                throw new ServiceException("Invalid username or password.");
            }
            if (u.getStatus() != null && !UserAccount.ACTIVE.equalsIgnoreCase(u.getStatus())) {
                throw new ServiceException("This account is inactive. Please contact the hotel.");
            }
            return toLoginUser(u);
        });
    }

    private LoginUser toLoginUser(UserAccount u) {
        String role = u.getRole() == null ? "" : u.getRole().trim().toUpperCase();
        Integer customerId = u.getCustomer() == null ? null : u.getCustomer().getCustomerId();
        Integer employeeId = u.getEmployee() == null ? null : u.getEmployee().getEmployeeId();
        String display = u.getUsername();
        if (u.getCustomer() != null && u.getCustomer().getName() != null) display = u.getCustomer().getName();
        else if (u.getEmployee() != null && u.getEmployee().getName() != null) display = u.getEmployee().getName();
        return new LoginUser(u.getUserId(), u.getUsername(), role, customerId, employeeId, display);
    }

    /** Customer self-registration: creates the Customer row and the login account in ONE transaction. */
    public void register(String name, String phone, String email, String address,
                         String username, String password, String confirm) {
        String n = Validator.required(name, "Name", 100);
        String ph = Validator.phone(phone, true);
        String em = Validator.email(email, true);
        String addr = Validator.optional(address, "Address", 255);
        String un = Validator.username(username);
        Validator.password(password);
        if (!password.equals(confirm)) throw new ServiceException("Passwords do not match.");

        HibernateUtil.txRun(session -> {
            if (userDAO.findByUsername(session, un) != null) throw new ServiceException("That username is already taken.");
            if (customerDAO.findByEmail(session, em) != null) {
                throw new ServiceException("An account with this email already exists. Please log in instead.");
            }
            Customer c = new Customer();
            c.setName(n);
            c.setPhone(ph);
            c.setEmail(em);
            c.setAddress(addr);
            customerDAO.save(session, c);

            UserAccount u = new UserAccount();
            u.setUsername(un);
            u.setPasswordHash(PasswordUtil.hash(password));
            u.setRole(UserAccount.ROLE_CUSTOMER);
            u.setCustomer(c);
            u.setStatus(UserAccount.ACTIVE);
            userDAO.save(session, u);
        });
    }

    public void changePassword(Integer userId, String current, String newPassword, String confirm) {
        Validator.password(newPassword);
        if (!newPassword.equals(confirm)) throw new ServiceException("New passwords do not match.");
        HibernateUtil.txRun(session -> {
            UserAccount u = userDAO.findById(session, userId);
            if (u == null || !PasswordUtil.matches(current, u.getPasswordHash())) {
                throw new ServiceException("Current password is incorrect.");
            }
            u.setPasswordHash(PasswordUtil.hash(newPassword));
        });
    }

    /**
     * Creates the first ADMIN account only when NO admin exists yet.
     * Called at startup when ADMIN_USERNAME and ADMIN_PASSWORD environment variables are set.
     * Returns true if an account was created.
     */
    public boolean createFirstAdminIfMissing(String username, String password) {
        return HibernateUtil.tx(session -> {
            if (userDAO.countByRole(session, UserAccount.ROLE_ADMIN) > 0) return false;
            if (userDAO.findByUsername(session, username) != null) return false;
            UserAccount u = new UserAccount();
            u.setUsername(Validator.username(username));
            u.setPasswordHash(PasswordUtil.hash(Validator.password(password)));
            u.setRole(UserAccount.ROLE_ADMIN);
            u.setStatus(UserAccount.ACTIVE);
            userDAO.save(session, u);
            return true;
        });
    }
}
