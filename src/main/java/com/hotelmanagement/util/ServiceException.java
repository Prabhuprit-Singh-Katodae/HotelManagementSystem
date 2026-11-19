package com.hotelmanagement.util;

/** A problem that is safe to show to the user (validation error, rule violation, etc.). */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
