package com.carsil.userapi.controller;

import com.carsil.userapi.exception.ApiError;
import com.carsil.userapi.model.Module;
import com.carsil.userapi.service.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    // Obtener todos los módulos
    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        List<Module> modules = moduleService.getAll();
        if (modules.isEmpty()) {
            ApiError error = new ApiError(HttpStatus.NOT_FOUND, "No se encontraron módulos.", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(modules);
    }

    // Buscar módulo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        return moduleService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ApiError error = new ApiError(HttpStatus.NOT_FOUND, "Módulo no encontrado con ID: " + id, request.getRequestURI());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    // Buscar módulos por nombre
    @GetMapping("/by-name")
    public ResponseEntity<?> findByName(@RequestParam String name, HttpServletRequest request) {
        List<Module> modules = moduleService.findByName(name);
        if (modules.isEmpty()) {
            ApiError error = new ApiError(HttpStatus.NOT_FOUND, "No se encontraron módulos con el nombre: " + name, request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(modules);
    }

    // Crear módulo
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Module module) {
        Module created = moduleService.create(module);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Actualizar módulo
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Module input, HttpServletRequest request) {
        return moduleService.update(id, input)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ApiError error = new ApiError(HttpStatus.NOT_FOUND, "No se pudo actualizar. Módulo con ID " + id + " no existe.", request.getRequestURI());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }
}