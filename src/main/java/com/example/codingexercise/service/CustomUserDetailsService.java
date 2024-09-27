package com.example.codingexercise.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // For simplicity, using hardcoded user details. In a real application, fetch user details from a database.
        if ("user".equals(username)) {
            return new User("user", "$2a$10$68j6AhlYfYNjbqyXpfhoaOwABV.VuOP1qLrlLiUGZ6FZTuHadv74K", Collections.emptyList()); // password: password
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
