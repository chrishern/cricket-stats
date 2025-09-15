package com.example.cricketstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.cricketstats")
public class CricketStatsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CricketStatsApplication.class, args);
    }
}