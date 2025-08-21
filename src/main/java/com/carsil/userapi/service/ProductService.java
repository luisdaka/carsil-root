package com.carsil.userapi.service;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Product update(Product product, Long id) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantity(product.getQuantity());
                    existingProduct.setAssignedDate(product.getAssignedDate());
                    existingProduct.setPlantEntryDate(product.getPlantEntryDate());
                    existingProduct.setReference(product.getReference());
                    existingProduct.setBrand(product.getBrand());
                    existingProduct.setOp(product.getOp());
                    existingProduct.setCampaign(product.getCampaign());
                    existingProduct.setType(product.getType());
                    existingProduct.setSize(product.getSize());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + product.getId()));
    }

    public List<Product> search(String q) {
        return productRepository.search(Optional.ofNullable(q).orElse("").trim());
    }
}