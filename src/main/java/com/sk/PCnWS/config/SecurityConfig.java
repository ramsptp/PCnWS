package com.sk.PCnWS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * This Bean creates the Password Hashing tool.
     * We'll use this in our UserService to protect passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This Bean is the main security filter.
     * It configures all your web security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // <-- ADD THIS LINE
            
            .authorizeHttpRequests(authorize -> authorize
                // Allow anyone to visit the login, register, and static files
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                // All other URLs (like "/" or "/add-plant") MUST be authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Use our custom login page
                .loginPage("/login")
                // This is the URL the login form will POST to
                .loginProcessingUrl("/login") 
                // After a successful login, send the user to the dashboard
                .defaultSuccessUrl("/", true) 
                .permitAll()
            )
            .logout(logout -> logout
                // Allow logging out
                .permitAll()
            );

        return http.build();
    }
}