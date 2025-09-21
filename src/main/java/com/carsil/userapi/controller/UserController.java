package com.carsil.userapi.controller;

import com.carsil.userapi.exception.ApiError;
import com.carsil.userapi.exception.ResourceNotFoundException;
import com.carsil.userapi.model.User;
import com.carsil.userapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Usuarios no encontrados",
                    "No hay registros de usuarios en el sistema.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(users);
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        try {
            User created = userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al crear usuario",
                    "El nombre de usuario o correo ya está registrado. "
                             + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Datos inválidos",
                    "No se pudo crear el usuario: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno",
                    "Ocurrió un problema inesperado al crear el usuario. " +
                            "Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody User user,
                                        HttpServletRequest request) {
        try {
            User updated = userService.update(id, user);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado",
                    "No se pudo actualizar: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al actualizar usuario",
                    "El nombre de usuario o correo ya existe. " +
                            "Detalles: " + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Datos inválidos",
                    "No se pudo actualizar el usuario: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno",
                    "Ocurrió un problema inesperado al actualizar el usuario con ID " + id + ".",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado",
                    "No se pudo eliminar: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno",
                    "Ocurrió un problema inesperado al eliminar el usuario con ID " + id + ".",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        try {
            User user = userService.getByIdOrThrow(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno",
                    "Ocurrió un problema inesperado al buscar el usuario con ID " + id + ".",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Buscar usuario por nombre
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name, HttpServletRequest request) {
        try {
            User user = userService.getByNameOrThrow(name);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno",
                    "Ocurrió un problema inesperado al buscar el usuario con nombre '" + name + "'.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}