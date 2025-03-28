package com.example.idpbackend.config;

import jakarta.servlet.http.HttpServletRequest; // Используйте этот импорт
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Отключите CSRF (если не нужен)
                .authorizeHttpRequests(auth -> auth
                        // Разрешите доступ к эндпоинтам без аутентификации
                        .requestMatchers("/api/github/**").permitAll() // Разрешите все эндпоинты /api/github
                        .requestMatchers("/public/**").permitAll() // Разрешите все эндпоинты /public
                        .anyRequest().permitAll()  // Все остальные запросы требуют аутентификации
                ).addFilterBefore((request, response, chain) -> {
                    HttpServletRequest httpRequest = (HttpServletRequest) request; // Приводим request к HttpServletRequest
                    System.out.println("Request URI: " + httpRequest.getRequestURI());
                    System.out.println("Authorization Header: " + httpRequest.getHeader("Authorization"));
                    System.out.println("Request Method: " + httpRequest.getMethod());
                    System.out.println("Remote Address: " + httpRequest.getRemoteAddr());
                    System.out.println("User Agent: " + httpRequest.getHeader("User-Agent"));
                    chain.doFilter(request, response);
                }, BasicAuthenticationFilter.class);
        return http.build();
    }
}