package com.blackcat.cricketstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.blackcat.cricketstats")
public class CricketStatsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CricketStatsApplication.class, args);
    }
}