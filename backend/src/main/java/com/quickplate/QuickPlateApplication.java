package com.quickplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@SpringBootApplication
public class QuickPlateApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuickPlateApplication.class, args);
    }
}
