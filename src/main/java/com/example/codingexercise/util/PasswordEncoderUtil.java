package com.example.codingexercise.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "pass"; // Replace with your actual password
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}