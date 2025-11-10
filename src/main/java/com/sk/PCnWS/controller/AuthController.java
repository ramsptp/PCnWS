package com.sk.PCnWS.controller;

import com.sk.PCnWS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // This is a Spring Web Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // --- Registration ---
    
    /**
     * This method shows the registration page.
     * It maps to the URL: http://localhost:8080/register
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        // This will return a file named "register.html" from our templates
        return "register";
    }

    /**
     * This method handles the form submission from the registration page.
     * It maps to a POST request to: http://localhost:8080/register
     */
    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password) {
        
        // Use the service to create the new user
        userService.registerUser(username, email, password);
        
        // After registering, send the user to the login page
        return "redirect:/login"; 
    }

    // --- Login ---

    /**
     * This method shows the login page.
     * It maps to the URL: http://localhost:8080/login
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Returns login.html
    }

    // Note: A real POST /login is complex and requires Spring Security
    // to handle sessions. For this 10-mark project, we will
    // skip the secure login and just build the dashboard directly.
}