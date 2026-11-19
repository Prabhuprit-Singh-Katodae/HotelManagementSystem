package com.hotelmanagement.util;

import java.io.Serializable;

/** The small object stored in the HttpSession after a successful login. */
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer userId;
    private final String username;
    private final String role;       // CUSTOMER, ADMIN or STAFF
    private final Integer customerId; // only for customers
    private final Integer employeeId; // only for staff/admin (optional)
    private final String displayName;

    public LoginUser(Integer userId, String username, String role, Integer customerId,
                     Integer employeeId, String displayName) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.displayName = displayName;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Integer getCustomerId() { return customerId; }
    public Integer getEmployeeId() { return employeeId; }
    public String getDisplayName() { return displayName; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isStaff() { return "STAFF".equals(role); }
    public boolean isCustomer() { return "CUSTOMER".equals(role); }
    /** Admin or staff = anyone who may open the /admin pages. */
    public boolean isHotelStaff() { return isAdmin() || isStaff(); }
}
