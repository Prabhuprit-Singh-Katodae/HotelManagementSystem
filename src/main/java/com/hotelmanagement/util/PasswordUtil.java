package com.hotelmanagement.util;

import org.mindrot.jbcrypt.BCrypt;

/** BCrypt password hashing. Plain passwords are never stored. */
public final class PasswordUtil {

    private PasswordUtil() { }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean matches(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            return false; // stored value is not a valid BCrypt hash
        }
    }
}
