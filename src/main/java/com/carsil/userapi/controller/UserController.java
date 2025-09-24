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
                    "No hay usuarios registrados actualmente.",
                    "La lista de usuarios está vacía. No se encontraron registros en la base de datos.",
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
                    "El usuario no pudo ser creado porque ya existe un registro con los mismos datos.",
                    "Conflicto de datos: el nombre de usuario o el correo ya están registrados. Detalle: "
                            + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Los datos enviados no son válidos para crear un usuario.",
                    "Error de validación: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocurrió un problema al crear el usuario.",
                    "Error inesperado en createUser(). Causa: " + ex.getMessage(),
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
                    "El usuario que intentas actualizar no existe.",
                    "Usuario con ID " + id + " no encontrado para actualización. Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "No se pudo actualizar el usuario porque ya existe un registro con esos datos.",
                    "Conflicto de datos al actualizar: nombre de usuario o correo duplicados. Detalle: "
                            + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException ex) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Los datos enviados no son válidos para actualizar el usuario.",
                    "Error de validación en updateUser(): " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocurrió un problema inesperado al actualizar el usuario.",
                    "Error inesperado en updateUser() para ID " + id + ". Causa: " + ex.getMessage(),
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
                    "No se pudo eliminar el usuario porque no existe.",
                    "Usuario con ID " + id + " no encontrado en la base de datos. Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al intentar eliminar el usuario.",
                    "Error en deleteUser() para ID " + id + ". Causa: " + ex.getMessage(),
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
                    "El usuario con ese ID no existe.",
                    "No se encontró el usuario con ID " + id + ". Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al buscar el usuario.",
                    "Error en getUserById() para ID " + id + ". Causa: " + ex.getMessage(),
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
                    "No se encontró un usuario con ese nombre.",
                    "Usuario con nombre '" + name + "' no existe en la base de datos. Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al buscar el usuario.",
                    "Error en getUserByName() para nombre '" + name + "'. Causa: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}