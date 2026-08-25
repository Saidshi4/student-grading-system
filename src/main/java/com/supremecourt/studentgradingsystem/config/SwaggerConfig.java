package com.supremecourt.studentgradingsystem.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Authorization",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();

        Info info = new Info()
                .title("Student Grading System API")
                .version("1.0")
                .contact(contact)
                .description("API for Student Grading System — students, teachers, courses, grades, transcripts, and reports.");

        return new OpenAPI().info(info)
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
