package com.carsil.userapi.service;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.repository.ModuleRepository;
import com.carsil.userapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carsil.userapi.model.Module;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModuleRepository moduleRepo;

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
    public Product create(Product incoming) {
        if (incoming.getOp() != null && productRepository.existsByOp(incoming.getOp())) {
            throw new IllegalArgumentException("this OP already exists: " + incoming.getOp());
        }

        Product p = new Product();
        if (incoming.getModule() != null && incoming.getModule().getId() != null) {
            Long moduleId = incoming.getModule().getId();
            Module module = moduleRepo.findById(moduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Module Not Found " + moduleId));
            p.setModule(module);
        } else {
            p.setModule(null);
        }

        p.setPrice(incoming.getPrice());
        p.setQuantity(incoming.getQuantity());
        p.setAssignedDate(incoming.getAssignedDate());
        p.setPlantEntryDate(incoming.getPlantEntryDate());
        p.setReference(incoming.getReference());
        p.setBrand(incoming.getBrand());
        p.setOp(incoming.getOp());
        p.setCampaign(incoming.getCampaign());
        p.setType(incoming.getType());
        p.setSize(incoming.getSize());
        p.setDescription(incoming.getDescription());
        p.setSizeQuantities(incoming.getSizeQuantities());

        return productRepository.save(p);
    }

    @Transactional
    public Product update(Product product, Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    // Validación: OP única si cambió
                    if (!existing.getOp().equals(product.getOp())
                            && productRepository.existsByOpAndIdNot(product.getOp(), id)) {
                        throw new DuplicateKeyException("op already exists: " + product.getOp());
                    }

                    Module newModule = null;
                    if (product.getModule() != null && product.getModule().getId() != null) {
                        Long moduleId = product.getModule().getId();
                        newModule = moduleRepo.findById(moduleId)
                                .orElseThrow(() -> new IllegalArgumentException("Module Not Found " + moduleId));
                    }
                    existing.setModule(newModule);

                    // Copiar campos (los de TU entidad)
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
                    existing.setDescription(product.getDescription());
                    existing.setSizeQuantities(product.getSizeQuantities());

                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }


    @Transactional(readOnly = true)
    public List<Product> search(String q) {
        return productRepository.search(Optional.ofNullable(q).orElse("").trim());
    }

    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }
}