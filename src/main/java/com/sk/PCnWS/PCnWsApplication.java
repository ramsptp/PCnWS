package com.sk.PCnWS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- IMPORT THIS

@SpringBootApplication
@EnableScheduling // <-- ADD THIS ANNOTATION
public class PCnWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PCnWsApplication.class, args);
    }
}