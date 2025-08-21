package com.example.userapi.repository;

import com.example.userapi.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByReferenceContainingIgnoreCase(String reference);

    List<Product> findByCampaignContainingIgnoreCase(String campaign);

    List<Product> findByOpContainingIgnoreCase(String op);

    List<Product> findByBrandContainingIgnoreCase(String brand);
}
