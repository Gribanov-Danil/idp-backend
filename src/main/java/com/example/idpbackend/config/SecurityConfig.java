package com.example.idpbackend.config;

import jakarta.servlet.http.HttpServletRequest; // Используйте этот импорт
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Отключите CSRF (если не нужен)
                .authorizeHttpRequests(auth -> auth
                        // Разрешите доступ к эндпоинтам без аутентификации
                        .requestMatchers("/api/github/**").permitAll() // Разрешите все эндпоинты /api/github
                        .requestMatchers("/api/templates/**").permitAll() // Разрешите все эндпоинты /api/github
                        .requestMatchers("/api/**").permitAll() // Разрешите все эндпоинты /api/github
                        .requestMatchers("/public/**").permitAll() // Разрешите все эндпоинты /public
                        // Обновленные пути для OpenAPI
                        .requestMatchers("/v3/api-docs/**", 
                                       "/swagger-ui/**", 
                                       "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // Разрешаем доступ к эндпоинтам аутентификации
                        .anyRequest().authenticated()  // Все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form.disable()) // Отключаем стандартную форму логина
                .httpBasic(basic -> basic.disable()) // Отключаем HTTP Basic аутентификацию
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository())
                        .requireExplicitSave(true)
                )
                .addFilterBefore((request, response, chain) -> {
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

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}