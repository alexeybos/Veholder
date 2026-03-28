package org.skillsmart.veholder.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Veholder App System Api",
                description = "API системы управления автомобилями предприятия (Veholder)",
                version = "1.0.0",
                contact = @Contact(
                        name = "Alex",
                        email = "alex@alex.alex",
                        url = "http://localhost"
                )
        )
)
public class OpenApiConfig {
    // Конфигурация для Swagger
}