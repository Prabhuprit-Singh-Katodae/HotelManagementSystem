package com.hotelmanagement.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Simple input checks. Every method throws ServiceException with a clear message. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{7,15}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");

    private Validator() { }

    /** Returns the trimmed text, or throws if it is empty or longer than max. */
    public static String required(String value, String label, int max) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) throw new ServiceException(label + " is required.");
        if (v.length() > max) throw new ServiceException(label + " must be at most " + max + " characters.");
        return v;
    }

    /** Returns trimmed text or null when empty. Throws only if too long. */
    public static String optional(String value, String label, int max) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) return null;
        if (v.length() > max) throw new ServiceException(label + " must be at most " + max + " characters.");
        return v;
    }

    public static String phone(String value, boolean mandatory) {
        String v = value == null ? "" : value.trim().replace(" ", "").replace("-", "");
        if (v.isEmpty()) {
            if (mandatory) throw new ServiceException("Phone number is required.");
            return null;
        }
        if (!PHONE.matcher(v).matches()) throw new ServiceException("Enter a valid phone number (7-15 digits).");
        return v;
    }

    public static String email(String value, boolean mandatory) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            if (mandatory) throw new ServiceException("Email is required.");
            return null;
        }
        if (v.length() > 100 || !EMAIL.matcher(v).matches()) throw new ServiceException("Enter a valid email address.");
        return v;
    }

    public static String username(String value) {
        String v = value == null ? "" : value.trim();
        if (!USERNAME.matcher(v).matches()) {
            throw new ServiceException("Username must be 3-30 characters: letters, digits, dot or underscore.");
        }
        return v;
    }

    public static String password(String value) {
        if (value == null || value.length() < 8) throw new ServiceException("Password must be at least 8 characters.");
        if (value.length() > 72) throw new ServiceException("Password must be at most 72 characters.");
        return value;
    }

    public static BigDecimal money(BigDecimal value, String label, boolean allowZero) {
        if (value == null) throw new ServiceException(label + " is required.");
        if (value.signum() < 0 || (!allowZero && value.signum() == 0)) {
            throw new ServiceException(label + " must be greater than " + (allowZero ? "or equal to " : "") + "zero.");
        }
        if (value.compareTo(new BigDecimal("99999999")) > 0) throw new ServiceException(label + " is too large.");
        return value;
    }
}
