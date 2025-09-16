package com.carsil.userapi.service;

import com.carsil.userapi.model.Module;
import com.carsil.userapi.model.Product;
import com.carsil.userapi.model.enums.ProductionStatus;
import com.carsil.userapi.repository.ModuleRepository;
import com.carsil.userapi.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product does not exist: " + id));
        productRepository.delete(p);
    }

    @Transactional
    public Product create(Product p) {
        if (p.getStatus() == null) p.setStatus(ProductionStatus.PROCESO);
        if (p.getQuantityMade() == null) p.setQuantityMade(0);
        if (p.getQuantity() == null)
            throw new IllegalArgumentException("quantity is required");

        recalcDerived(p);

        return productRepository.save(p);
    }

    @Transactional
    public Product update(Product patch,Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        if (patch.getPrice() != null) existing.setPrice(patch.getPrice());
        if (patch.getQuantity() != null) existing.setQuantity(patch.getQuantity());
        if (patch.getAssignedDate() != null) existing.setAssignedDate(patch.getAssignedDate());
        if (patch.getPlantEntryDate() != null) existing.setPlantEntryDate(patch.getPlantEntryDate());
        if (patch.getReference() != null) existing.setReference(patch.getReference());
        if (patch.getBrand() != null) existing.setBrand(patch.getBrand());
        if (patch.getCampaign() != null) existing.setCampaign(patch.getCampaign());
        if (patch.getType() != null) existing.setType(patch.getType());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getSizeQuantities() != null) existing.setSizeQuantities(patch.getSizeQuantities());
        if (patch.getSam() != null) existing.setSam(patch.getSam());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (patch.getStoppageReason() != null) existing.setStoppageReason(patch.getStoppageReason());
        if (patch.getModule() != null) existing.setModule(patch.getModule());

        if (patch.getOp() != null && !patch.getOp().equals(existing.getOp())
                && productRepository.existsByOpAndIdNot(patch.getOp(), id)) {
            throw new org.springframework.dao.DuplicateKeyException("op already exists: " + patch.getOp());
        }
        if (patch.getQuantityMade() != null) {
            int delta = patch.getQuantityMade() - existing.getQuantityMade();
            existing.addMade(delta);
        }

        recalcDerived(existing);
        try {
            return productRepository.save(existing);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Concurrent update detected for product " + id, e);
        }
    }


    @Transactional(readOnly = true)
    public List<Product> search(String q) {
        return productRepository.search(Optional.ofNullable(q).orElse("").trim());
    }

    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByModule(Long moduleId) {
        return productRepository.findByModuleId(moduleId);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByOp(String op) {
        return productRepository.findByOp(op);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByDateRange(LocalDate startDate, LocalDate endDate) {
        return productRepository.findByPlantEntryDateBetween(startDate, endDate);
    }

    @Transactional
    public Product setMade(Long id, int newValue) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        int delta = newValue - (p.getQuantityMade() == null ? 0 : p.getQuantityMade());
        p.addMade(delta);
        recalcDerived(p);
        return productRepository.save(p);
    }

    private void recalcDerived(Product p) {
        // missing = quantity - quantityMade
        if (p.getQuantity() != null) {
            int made = (p.getQuantityMade() == null ? 0 : p.getQuantityMade());
            p.setMissing(Math.max(0, p.getQuantity() - made));
        }
        // samTotal = missing * sam
        if (p.getSam() != null && p.getMissing() != null) {
            p.setSamTotal((int) Math.round(p.getMissing() * p.getSam()));
        }
        // status default si faltó
        if (p.getStatus() == null) p.setStatus(ProductionStatus.PROCESO);
    }

    @Transactional
    public Product incrementMade(Long id, int delta) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        if (delta != 0) {
            p.addMade(delta);
        }

        recalcDerived(p);
        return productRepository.save(p);
    }
}