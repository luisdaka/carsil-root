package com.carsil.userapi.service;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.model.enums.ProductionStatus;
import com.carsil.userapi.repository.ModuleRepository;
import com.carsil.userapi.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModuleRepository moduleRepository;

feature/error-handling
    private static final String REGEX_VALIDATION = "^[a-zA-Z0-9 ]+$";

    private void validate(Product p) {
        if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (p.getCampaign() == null || p.getCampaign().trim().isEmpty()) {
            throw new IllegalArgumentException("La campaña no puede estar vacía");
        }
        if (p.getDescription() != null && !p.getDescription().matches(REGEX_VALIDATION)) {
            throw new IllegalArgumentException("La descripción contiene caracteres no permitidos");
        }
        if (p.getBrand() != null && !p.getBrand().matches(REGEX_VALIDATION)) {
            throw new IllegalArgumentException("La marca contiene caracteres no permitidos");
        }
        if (p.getType() != null && !p.getType().matches(REGEX_VALIDATION)) {
            throw new IllegalArgumentException("El tipo contiene caracteres no permitidos");
        }
        if (p.getQuantity() == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria");
        }
    }

    @Autowired
    private ObjectMapper objectMapper;
 main

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    private static final Set<String> IMMUTABLE_FIELDS = Set.of(
            "id"
    );

    private static final String MODULE_ID_KEY = "moduleId";

    @Transactional
    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe: " + id));
        productRepository.delete(p);
    }

    @Transactional
    public Product create(Product p) {
        validate(p);
        if (p.getStatus() == null) p.setStatus(ProductionStatus.PROCESO);
        if (p.getQuantityMade() == null) p.setQuantityMade(0);
 feature/error-handling
        recalcDerived(p);

        if (p.getQuantity() == null)
            throw new IllegalArgumentException("quantity is required");
        if(p.getModule() != null){
            var m = moduleRepository.findById(p.getModule().getId()).get();
            m.setLoadDays(p.getLoadDays());
            moduleRepository.save(m);
        }
         recalcDerived(p);
 main
        return productRepository.save(p);
    }

    @Transactional
    public Product update(Product patch, Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

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
        if (patch.getActualDeliveryDate() != null) existing.setActualDeliveryDate(patch.getActualDeliveryDate());
        if (patch.getModule() != null) existing.setModule(patch.getModule());
        if (patch.getOp() != null && !patch.getOp().equals(existing.getOp())
                && productRepository.existsByOpAndIdNot(patch.getOp(), id)) {
            throw new IllegalArgumentException("El OP ya existe: " + patch.getOp());
        }
        if (patch.getQuantityMade() != null) {
            int delta = patch.getQuantityMade() - existing.getQuantityMade();
            existing.addMade(delta);
        }

        validate(existing);
        recalcDerived(existing);
        try {
            return productRepository.save(existing);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Actualización concurrente detectada para el producto " + id, e);
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
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        int delta = newValue - (p.getQuantityMade() == null ? 0 : p.getQuantityMade());
        p.addMade(delta);
        validate(p);
        recalcDerived(p);
        return productRepository.save(p);
    }

    private void recalcDerived(Product p) {
        if (p.getQuantity() != null) {
            int made = (p.getQuantityMade() == null ? 0 : p.getQuantityMade());
            p.setMissing(Math.max(0, p.getQuantity() - made));
        }
        if (p.getSam() != null && p.getMissing() != null) {
            p.setSamTotal((int) Math.round(p.getMissing() * p.getSam()));
        }
        if (p.getStatus() == null) p.setStatus(ProductionStatus.PROCESO);
        if(p.getModule() != null){
            Module m = moduleRepository.findById(p.getModule().getId()).get();
            m.setLoadDays(p.getLoadDays());
            moduleRepository.save(m);
        }
    }

    @Transactional
    public Product incrementMade(Long id, int delta) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        if (delta != 0) {
            p.addMade(delta);
        }
        validate(p);
        recalcDerived(p);
        return productRepository.save(p);
    }

    @Transactional
    public Product partialUpdate(Long id, Map<String, Object> updates) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        if (updates == null || updates.isEmpty()) {
            return existing;
        }

        Map<String, Object> sanitized = new HashMap<>(updates);
        IMMUTABLE_FIELDS.forEach(sanitized::remove);

        if (sanitized.containsKey(MODULE_ID_KEY)) {
            Object raw = sanitized.remove(MODULE_ID_KEY);
            if (raw != null) {
                Long moduleId = toLong(raw);
                Module module = moduleRepository.findById(moduleId)
                        .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));
                existing.setModule(module);
            } else {
                existing.setModule(null);
            }
        }
        try {
            objectMapper.updateValue(existing, sanitized);
        } catch (IllegalArgumentException | JsonMappingException e) {
            throw new RuntimeException("Invalid PATCH payload: " + e.getMessage(), e);
        }

        recalcDerived(existing);

        return productRepository.save(existing);
    }

    private Long toLong(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Number n) return n.longValue();
        return Long.valueOf(String.valueOf(raw));
    }
}