package com.hotelmanagement.util;

/**
 * Small command-line helper that prints a BCrypt hash for a password.
 * Use it to insert the first ADMIN account with SQL (see README).
 *
 * Run from NetBeans: right-click this file > Run File, or use the command line.
 * Usage: HashPasswordTool YourPasswordHere
 */
public class HashPasswordTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: HashPasswordTool <password>");
            return;
        }
        System.out.println(PasswordUtil.hash(args[0]));
    }
}
