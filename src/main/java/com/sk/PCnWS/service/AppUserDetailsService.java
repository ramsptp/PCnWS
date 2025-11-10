package com.sk.PCnWS.service;

import com.sk.PCnWS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * This is the only method Spring Security needs.
     * It's called when a user tries to log in.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Find our user in our own database
        com.sk.PCnWS.model.User ourUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Convert our User object into a Spring Security UserDetails object
        // Spring Security will take this object and automatically check
        // if the password matches the one the user typed in.
        return org.springframework.security.core.userdetails.User
                .withUsername(ourUser.getUsername())
                .password(ourUser.getPassword()) // Give it the HASHED password from our DB
                .authorities(new ArrayList<>()) // We can add roles (like "ROLE_USER") here later
                .build();
    }
}