package com.example.idpbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Разрешите CORS для всех эндпоинтов /api
                .allowedOrigins("*") // Разрешите все источники
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Разрешите методы
                .allowedHeaders("*"); // Разрешите все заголовки
    }
}