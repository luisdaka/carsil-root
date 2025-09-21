package com.carsil.userapi.controller;

import com.carsil.userapi.exception.ApiError;
import com.carsil.userapi.model.Module;
import com.carsil.userapi.service.ModuleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron módulos",
                    "La base de datos no contiene registros de módulos actualmente.",
                    request.getRequestURI()
            );
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
                    ApiError error = new ApiError(
                            HttpStatus.NOT_FOUND,
                            "Módulo no encontrado",
                            "No existe un módulo registrado con el ID " + id + ". " +
                                    "Verifica que el identificador sea correcto.",
                            request.getRequestURI()
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    // Buscar módulos por nombre
    @GetMapping("/by-name")
    public ResponseEntity<?> findByName(@RequestParam String name, HttpServletRequest request) {
        List<Module> modules = moduleService.findByName(name);
        if (modules.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Búsqueda sin resultados",
                    "No se encontraron módulos cuyo nombre coincida con: '" + name + "'. " +
                            "Verifica si el nombre está escrito correctamente.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(modules);
    }

    // Crear módulo
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Module module, HttpServletRequest request) {
        try {
            Module created = moduleService.create(module);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al crear el módulo",
                    "Ya existe un módulo con un valor único que intentas registrar. " +
                            "Detalles técnicos: " + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno al crear el módulo",
                    "Ocurrió un problema inesperado mientras se intentaba guardar el módulo. " +
                            "Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Actualizar módulo
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Module input, HttpServletRequest request) {
        try {
            return moduleService.update(id, input)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        ApiError error = new ApiError(
                                HttpStatus.NOT_FOUND,
                                "No se pudo actualizar",
                                "El módulo con ID " + id + " no existe en la base de datos. " +
                                        "Verifica el identificador antes de enviar la actualización.",
                                request.getRequestURI()
                        );
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al actualizar el módulo",
                    "El cambio que intentas realizar genera un conflicto con otro registro existente. " +
                            "Detalles: " + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno al actualizar el módulo",
                    "Se produjo un error inesperado mientras se actualizaba el módulo con ID " + id + ". " +
                            "Detalles: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}