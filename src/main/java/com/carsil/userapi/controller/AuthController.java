package com.carsil.userapi.controller;

import com.carsil.userapi.dto.LoginRequest;
import com.carsil.userapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        // 1. Validar campos no vacíos en el backend
        if (loginRequest.getUserName() == null || loginRequest.getUserName().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            response.put("error", "El nombre de usuario y la contraseña no pueden estar vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 2. Validar el nombre de usuario para caracteres especiales
        Pattern nameSpecialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        if (nameSpecialCharPattern.matcher(loginRequest.getUserName()).find()) {
            response.put("error", "El nombre de usuario no puede contener caracteres especiales.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 3. Validar la contraseña (solo letras y números)
        Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]+$");
        if (!passwordPattern.matcher(loginRequest.getPassword()).matches()) {
            response.put("error", "La contraseña solo puede contener letras y números.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        boolean authenticated = userService.validateLogin(loginRequest.getUserName(), loginRequest.getPassword());

        if (authenticated) {
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}