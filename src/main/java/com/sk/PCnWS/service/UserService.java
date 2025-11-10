package com.sk.PCnWS.service;

import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // Tells Spring this is a Service class
public class UserService {

    // @Autowired tells Spring to "inject" the repository here
    @Autowired
    private UserRepository userRepository;

    public User registerUser(String username, String email, String password) {
        // TODO: In a real app, you MUST hash the password before saving!
        // For this project, we'll save it as-is.
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        
        return userRepository.save(newUser);
    }

    public Optional<User> findByUsername(String username) {
        // This uses the method we defined in the UserRepository
        return userRepository.findByUsername(username);
    }
}