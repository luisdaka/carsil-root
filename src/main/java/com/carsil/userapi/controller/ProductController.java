package com.carsil.userapi.controller;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.service.ProductService;
import com.carsil.userapi.exception.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<Product> products = productService.getAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontraron productos", "/api/products"));
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product created = productService.create(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Error al crear el producto: " + ex.getMessage(), "/api/products"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontró el producto con id " + id, "/api/products/" + id));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.update(product, id);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se pudo actualizar. Producto no encontrado con id " + id, "/api/products/" + id));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam("q") String q) {
        List<Product> results = productService.search(q);
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontraron productos con la búsqueda: " + q, "/api/products/search"));
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontró el producto con id " + id, "/api/products/" + id)));
    }

    @GetMapping("/by-op/{op}")
    public ResponseEntity<?> getProductByOp(@PathVariable String op) {
        List<Product> products = productService.getProductsByOp(op);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontraron productos con op: " + op, "/api/products/by-op/" + op));
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<?> getProductsByModule(@PathVariable Long moduleId) {
        List<Product> products = productService.getProductsByModule(moduleId);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "No se encontraron productos para el módulo con id: " + moduleId, "/api/products/by-module/" + moduleId));
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<?> getProductsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Product> products = productService.getProductsByDateRange(startDate, endDate);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se encontraron productos entre " + startDate + " y " + endDate,
                            "/api/products/by-date-range"));
        }
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}/made")
    public ResponseEntity<?> setMade(@PathVariable Long id, @RequestParam("value") int value) {
        try {
            Product updated = productService.setMade(id, value);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se pudo actualizar el progreso. Producto no encontrado con id " + id,
                            "/api/products/" + id + "/made"));
        }
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<?> incrementProgress(@PathVariable Long id, @RequestParam int delta) {
        try {
            Product updated = productService.incrementMade(id, delta);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND,
                            "No se pudo incrementar el progreso. Producto no encontrado con id " + id,
                            "/api/products/" + id + "/progress"));
        }
    }
}