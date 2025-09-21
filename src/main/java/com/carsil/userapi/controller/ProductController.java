package com.carsil.userapi.controller;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.service.ProductService;
import com.carsil.userapi.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<?> getProducts(HttpServletRequest request) {
        List<Product> products = productService.getAll();
        if (products.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron productos.",
                    "Actualmente la base de datos no contiene productos registrados. " +
                            "Por favor, agregue al menos un producto antes de consultar.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(products);
    }

    // Crear producto
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product, HttpServletRequest request) {
        try {
            Product created = productService.create(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al crear el producto.",
                    "Ya existe un producto con un valor único (ejemplo: campo OP) que intentas registrar. " +
                            "Verifica que el valor sea único e intenta nuevamente. " +
                            "Detalles técnicos: " + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al crear el producto.",
                    "El servidor encontró un problema al intentar guardar el producto. " +
                            "Verifica los datos enviados o contacta al administrador si el error persiste. " +
                            "Detalles técnicos: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se pudo eliminar el producto.",
                    "El producto con ID " + id + " no existe en el sistema. " +
                            "Verifica que el ID sea correcto antes de intentar nuevamente.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        try {
            Product updated = productService.update(product, id);
            return ResponseEntity.ok(updated);
        } catch (DataIntegrityViolationException ex) {
            ApiError error = new ApiError(
                    HttpStatus.CONFLICT,
                    "Conflicto al actualizar el producto.",
                    "La actualización genera un conflicto con otro registro existente. " +
                            "Es probable que el valor de un campo único ya esté en uso. " +
                            "Detalles técnicos: " + ex.getMostSpecificCause().getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se pudo actualizar el producto.",
                    "El producto con ID " + id + " no existe o fue eliminado. " +
                            "Verifica el identificador e intenta nuevamente.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id, HttpServletRequest request) {
        return productService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ApiError error = new ApiError(
                            HttpStatus.NOT_FOUND,
                            "Producto no encontrado.",
                            "El producto con ID " + id + " no existe en la base de datos. " +
                                    "Asegúrate de usar un ID válido.",
                            request.getRequestURI()
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    // Buscar productos por texto libre
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam("q") String q, HttpServletRequest request) {
        List<Product> results = productService.search(q);
        if (results.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "Búsqueda sin resultados.",
                    "No se encontraron productos que coincidan con el criterio: '" + q + "'. " +
                            "Intenta con un nombre, código u otro valor válido.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(results);
    }

    // Buscar productos por OP
    @GetMapping("/by-op/{op}")
    public ResponseEntity<?> getProductByOp(@PathVariable String op, HttpServletRequest request) {
        List<Product> products = productService.getProductsByOp(op);
        if (products.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron productos con esa OP.",
                    "No existen registros de productos asociados a la OP: '" + op + "'. " +
                            "Verifica que el valor de la OP sea correcto.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(products);
    }

    // Buscar productos por módulo
    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<?> getProductsByModule(@PathVariable Long moduleId, HttpServletRequest request) {
        List<Product> products = productService.getProductsByModule(moduleId);
        if (products.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron productos asociados al módulo.",
                    "No existen productos registrados para el módulo con ID: " + moduleId + ". " +
                            "Verifica que el módulo exista antes de realizar esta consulta.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(products);
    }

    // Buscar productos por rango de fechas
    @GetMapping("/by-date-range")
    public ResponseEntity<?> getProductsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        List<Product> products = productService.getProductsByDateRange(startDate, endDate);
        if (products.isEmpty()) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se encontraron productos en el rango de fechas.",
                    "No existen productos registrados entre " + startDate + " y " + endDate + ". " +
                            "Verifica que las fechas ingresadas sean correctas y que existan registros en ese rango.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(products);
    }

    // Actualizar progreso manualmente
    @PutMapping("/{id}/made")
    public ResponseEntity<?> setMade(@PathVariable Long id, @RequestParam("value") int value, HttpServletRequest request) {
        try {
            Product updated = productService.setMade(id, value);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se pudo actualizar el progreso del producto.",
                    "El producto con ID " + id + " no fue encontrado. " +
                            "Verifica que el identificador sea correcto antes de intentar la actualización.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Incrementar progreso
    @PatchMapping("/{id}/progress")
    public ResponseEntity<?> incrementProgress(@PathVariable Long id, @RequestParam int delta, HttpServletRequest request) {
        try {
            Product updated = productService.incrementMade(id, delta);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            ApiError error = new ApiError(
                    HttpStatus.NOT_FOUND,
                    "No se pudo incrementar el progreso del producto.",
                    "El producto con ID " + id + " no existe o no se puede modificar. " +
                            "Verifica el identificador y vuelve a intentarlo.",
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}