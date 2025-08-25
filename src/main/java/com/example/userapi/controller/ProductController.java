package com.example.userapi.controller;

import com.example.userapi.model.product.Product;
import com.example.userapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    // Crear un producto
    @PostMapping("/create")
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    // Buscar por referencia
    @GetMapping("/search/reference/{reference}")
    public List<Product> searchByReference(@PathVariable String reference) {
        return (List<Product>) productService.findByReference(reference);
    }

    // Buscar por OP
    @GetMapping("/search/op/{op}")
    public List<Product> searchByOp(@PathVariable String op) {
        return productService.findByOp(op);
    }

    // Buscar por campaña
    @GetMapping("/search/campaign/{campaign}")
    public List<Product> searchByCampaign(@PathVariable String campaign) {
        return productService.findByCampaign(campaign);
    }

    // Buscar por marca
    @GetMapping("/search/brand/{brand}")
    public List<Product> searchByBrand(@PathVariable String brand) {
        return (List<Product>) productService.findByBrand(brand);
    }
}


