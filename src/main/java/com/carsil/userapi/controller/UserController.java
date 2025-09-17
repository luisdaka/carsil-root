package com.carsil.userapi.controller;

import com.carsil.userapi.exception.ApiError;
import com.carsil.userapi.model.User;
import com.carsil.userapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se encontraron usuarios",
                            request.getRequestURI()));
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        try {
            User created = userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST,
                            "Error al crear el usuario: " + ex.getMessage(),
                            request.getRequestURI()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody User user,
                                        HttpServletRequest request) {
        try {
            User updated = userService.update(id, user);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se pudo actualizar. Usuario con ID " + id + " no existe.",
                            request.getRequestURI()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se pudo eliminar. Usuario con ID " + id + " no existe.",
                            request.getRequestURI()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        try {
            User user = userService.getByIdOrThrow(id);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "Usuario no encontrado con ID: " + id,
                            request.getRequestURI()));
        }
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name, HttpServletRequest request) {
        try {
            User user = userService.getByNameOrThrow(name);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "Usuario no encontrado con nombre: " + name,
                            request.getRequestURI()));
        }
    }
}