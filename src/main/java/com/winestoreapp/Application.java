package com.winestoreapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("API Documentation Overview: http://localhost:8080/api/swagger-ui/index.html#/");
        log.info("Health check controller: http://localhost:8080/api/health");
        log.info("Health check html: file:///C:/Users/Artem/Documents/Java/UltimateJet"
                + "Brains/wine-store-app/frontend/public/index.html");
        // TODO: 30.01.2024 write logs in code
        //        https://www.youtube.com/watch?v=jPTIKU3VPQk
        //        https://dashboard.ngrok.com/get-started/setup/windows
        //        https://www.youtube.com/watch?v=NrSS6TCBP-Y
        //        https://docs.render.com/docker
    }
}
