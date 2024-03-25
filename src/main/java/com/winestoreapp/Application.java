package com.winestoreapp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application {
    @Value("${main.url}")
    private String mainUrl;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void logUrls() {
        log.info("Health check controller (without registration): "
                + mainUrl + "/api/health");
        log.info("\nAPI Documentation Overview: "
                + mainUrl + "/api/swagger-ui/index.html#/"
                + "\nDefault login for using documentation for Admin: "
                + "admin12345@gmail.com, and password: 12345"
                + "\nDefault login for using documentation for Manager: "
                + "manager12345@gmail.com, and password: 12345");
    }
}
