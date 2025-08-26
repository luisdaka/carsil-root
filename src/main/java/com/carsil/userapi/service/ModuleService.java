package com.carsil.userapi.service;

import com.carsil.userapi.model.Module;
import com.carsil.userapi.model.Product;
import com.carsil.userapi.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    public List<Module> getAll() {
        return moduleRepository.findAll();
    }

    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }

    public List<Module> findByName(String name) {
        return moduleRepository.findByNameContainingIgnoreCase(name);
    }

    public Module create(Module product) {
        return moduleRepository.save(product);
    }
}