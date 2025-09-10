package com.carsil.userapi.controller;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Endpoint: Buscar productos por módulo
    @GetMapping("/by-module/{moduleId}")
    public ResponseEntity<List<Product>> getProductsByModule(@PathVariable Long moduleId) {
        List<Product> products = productService.getProductsByModule(moduleId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si no hay productos, devuelve 204
        }
        return ResponseEntity.ok(products);
    }

    // Endpoint: Buscar productos por rango de fechas con paginación y ordenamiento
    @GetMapping("/by-date-range")
    public ResponseEntity<Page<Product>> getProductsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<Product> products = productService.getProductsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(product, id);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam("q") String q) {
        return productService.search(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}