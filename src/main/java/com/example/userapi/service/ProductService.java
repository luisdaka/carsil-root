package com.example.userapi.service;

import com.example.userapi.model.product.Product;
import com.example.userapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public com.example.userapi.service.ProductServiceroductService findByReference(String reference) {
        return null;
    }

    public List<Product> findByOp(String op) {
        return List.of();
    }

    public List<Product> findByCampaign(String campaign) {
        return List.of();
    }

    public com.example.userapi.service.ProductServiceroductService findByBrand(String brand) {
        return null;
    }

        public void productService(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        public ProductService(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        public List<Product> searchByOp(String op) {
            return productRepository.findByOpContainingIgnoreCase(op);
        }

        public List<Product> searchByReference(String reference) {
            return productRepository.findByReferenceContainingIgnoreCase(reference);
        }

        public List<Product> searchByCampaign(String campaign) {
            return productRepository.findByCampaignContainingIgnoreCase(campaign);
        }

        public List<Product> searchByBrand(String brand) {
            return productRepository.findByBrandContainingIgnoreCase(brand);
        }
    }
