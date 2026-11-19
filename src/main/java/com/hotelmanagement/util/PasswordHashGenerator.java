/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotelmanagement.util;

/**
 *
 * @author Prabhuprit Singh
 */


import java.util.Scanner;

public class PasswordHashGenerator {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter temporary password: ");
        String password = sc.nextLine();

        String hash = PasswordUtil.hash(password);

        System.out.println("Generated BCrypt Hash:");
        System.out.println(hash);
    }
}
