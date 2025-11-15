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
            
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " + 
                        
                        // Scripts: Allow 'self', inline, Tailwind, and WeatherWidget
                        "script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com https://weatherwidget.io https://forecast7.com; " +
                        
                        // Styles: Allow 'self', inline, and WeatherWidget
                        "style-src 'self' 'unsafe-inline' https://weatherwidget.io; " +
                        
                        // Images: Allow 'self', data:, and all external sites (*)
                        "img-src 'self' data: *; " + 
                        
                        // Frames: Allow the WeatherWidget to embed
                        "frame-src 'self' https://weatherwidget.io https://forecast7.com; " +

                        // 
                        // --- THIS IS THE UPDATED LINE ---
                        // We must allow *both* Open-Meteo APIs
                        //
                        "connect-src 'self' https://api.open-meteo.com https://geocoding-api.open-meteo.com"
                    )
                )
            )

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/register").permitAll()
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