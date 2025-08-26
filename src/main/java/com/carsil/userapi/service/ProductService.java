package com.carsil.userapi.service;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Product create(Product product) {
        if (productRepository.existsByOp(product.getOp())) {
            throw new DuplicateKeyException("op already exists: " + product.getOp());
        }
        return productRepository.save(product);
    }

    public Product update(Product product, Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    if (!existing.getOp().equals(product.getOp())
                            && productRepository.existsByOpAndIdNot(product.getOp(), id)) {
                        throw new DuplicateKeyException("op already exists: " + product.getOp());
                    }
                    existing.setPrice(product.getPrice());
                    existing.setQuantity(product.getQuantity());
                    existing.setAssignedDate(product.getAssignedDate());
                    existing.setPlantEntryDate(product.getPlantEntryDate());
                    existing.setReference(product.getReference());
                    existing.setBrand(product.getBrand());
                    existing.setOp(product.getOp());
                    existing.setCampaign(product.getCampaign());
                    existing.setType(product.getType());
                    existing.setSize(product.getSize());
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }


    public List<Product> search(String q) {
        return productRepository.search(Optional.ofNullable(q).orElse("").trim());
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }
}