package com.sk.PCnWS.service;

import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- IMPORT THIS
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // <-- INJECT THE PASSWORD ENCODER

    public User registerUser(String username, String email, String password) {
        
        // HASH THE PASSWORD before saving
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword); // <-- SAVE THE HASHED PASSWORD
        
        return userRepository.save(newUser);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}