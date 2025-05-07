package com.quickplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
  info = @Info(
    title = "QuickPlate REST API",
    version = "v1",
    description = "Dokumentacja endpointów użytkowników, restauracji, zamówień itd."
  )
)
@SpringBootApplication
public class QuickPlateApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuickPlateApplication.class, args);
    }
}
