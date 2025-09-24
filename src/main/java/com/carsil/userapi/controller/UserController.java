package com.carsil.userapi.controller;

import com.carsil.userapi.service.UserService;
import com.carsil.userapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // 1. Validar campos obligatorios
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario no puede estar vacío.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña no puede estar vacía.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email no puede estar vacío.");
        }

        // 2. Validar que el nombre de usuario no contenga caracteres especiales
        Pattern nameSpecialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        if (nameSpecialCharPattern.matcher(user.getName()).find()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario no puede contener caracteres especiales.");
        }

        // 3. Validar la contraseña (solo letras y números)
        Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]+$");
        if (!passwordPattern.matcher(user.getPassword()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña solo puede contener letras y números.");
        }

        // 4. Validar el formato del email
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El formato del email es inválido.");
        }

        // 5. Validar si el usuario ya existe (unicidad)
        Optional<User> existingUser = userService.getByName(user.getName());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario '" + user.getName() + "' ya existe.");
        }

        // Si las validaciones pasan, crea el usuario
        User createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.create(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        return userService.getByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}