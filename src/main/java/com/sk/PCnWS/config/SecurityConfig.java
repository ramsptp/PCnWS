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
            .csrf(csrf -> csrf.disable()) // For our forms to work
            
            // --- UPDATED SECURITY HEADERS ---
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        // Default: allow from our own server
                        "default-src 'self'; " + 
                        
                        // Scripts: Allow 'self', inline scripts, Tailwind, and BOTH weather domains
                        "script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com https://weatherwidget.io https://forecast7.com; " +
                        
                        // Styles: Allow 'self', inline styles, and the weather widget
                        "style-src 'self' 'unsafe-inline' https://weatherwidget.io; " +
                        
                        // Images: Allow 'self', data:, and all external sites (*)
                        "img-src 'self' data: *; " + 
                        
                        // Frames: Allow the weather widget to embed
                        "frame-src 'self' https://weatherwidget.io https://forecast7.com; " +

                        // Connect: Allow 'self' and BOTH weather domains
                        "connect-src 'self' https://weatherwidget.io https://forecast7.com"
                    )
                )
            )
            // --- END OF UPDATED BLOCK ---

            .authorizeHttpRequests(authorize -> authorize
                // Allow anyone to visit login and register
                .requestMatchers("/login", "/register").permitAll()
                
                // All other URLs MUST be authenticated
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}