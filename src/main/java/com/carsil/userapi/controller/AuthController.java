package com.carsil.userapi.controller;

import com.carsil.userapi.dto.LoginRequest;
import com.carsil.userapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        userService.validateLogin(req.getUserName(), req.getPassword());
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
