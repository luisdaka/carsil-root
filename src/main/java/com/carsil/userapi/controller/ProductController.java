package com.carsil.userapi.controller;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAll();
    }

    // Crear producto asignando usuario
    @PostMapping
    public Product createProduct(@RequestBody Product product, @RequestParam Long userId) {
        return productService.create(product, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    // Actualizar producto asignando usuario
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product, @RequestParam Long userId) {
        return productService.update(product, id, userId);
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
