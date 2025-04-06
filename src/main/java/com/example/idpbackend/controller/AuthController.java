package com.example.idpbackend.controller;

import com.example.idpbackend.dto.auth.LoginRequest;
import com.example.idpbackend.dto.auth.RegistrationRequest;
import com.example.idpbackend.entity.User;
import com.example.idpbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegistrationRequest registrationRequest, HttpServletRequest request) {
        ResponseEntity<Map<String, String>> authResponse = checkIfAuthenticated(request);
        if (authResponse != null) {
            return authResponse;
        }

        try {
            User existingUser = userService.loadUserByUsername(registrationRequest.getEmail());

            return new ResponseEntity<>(Map.of("message", "Пользователь с такой почтой уже существует"), HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            if (!userService.validateEmail(registrationRequest.getEmail())) {
                return new ResponseEntity<>(Map.of("message", "Неверный формат почты"), HttpStatus.BAD_REQUEST);
            }

            User newUser = new User(registrationRequest.getEmail(), registrationRequest.getPassword());
            userService.setPassword(newUser);
            userService.save(newUser);

            if (authUser(newUser.getEmail(), registrationRequest.getPassword(), request)) {
                return new ResponseEntity<>(Map.of("message", "Пользователь успешно зарегистрирован"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Ошибка при регистрации"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseEntity<Map<String, String>> authResponse = checkIfAuthenticated(request);
        if (authResponse != null) {
            return authResponse;
        }

        try {
            User user = userService.loadUserByUsername(loginRequest.getEmail());

            if (authUser(user.getEmail(), loginRequest.getPassword(), request)) {
                return new ResponseEntity<>(Map.of("message", "Авторизация прошла успешно"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Ошибка при авторизации"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return new ResponseEntity<>(Map.of("message", "Почта или пароль неверны"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        return new ResponseEntity<>(Map.of("message", "Вы успешно вышли из системы"), HttpStatus.OK);
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkAuthentication(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(request)) {
            return new ResponseEntity<>(Map.of("message", "Пользователь авторизован как: " + authentication.getName()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "Пользователь не авторизован"), HttpStatus.UNAUTHORIZED);
        }
    }

    // Метод для авторизации пользователя
    private Boolean authUser(String email, String password, HttpServletRequest request) throws BadCredentialsException {        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        if (!authentication.isAuthenticated()) {
            return false;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        // Пишем информацию о входе для истории
        logger.info("Authenticated user: {}, from ip: {}", authentication.getName(), request.getRemoteAddr());

        return true;
    }

    // Метод для проверки авторизации пользователя
    private Boolean isAuthenticated(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return true;
        }

        return false;
    }

    private ResponseEntity<Map<String, String>> checkIfAuthenticated(HttpServletRequest request) {
        if (isAuthenticated(request)) {
            return new ResponseEntity<>(Map.of("message", "Пользователь уже авторизован"), HttpStatus.FORBIDDEN);
        }
        return null;
    }
}
