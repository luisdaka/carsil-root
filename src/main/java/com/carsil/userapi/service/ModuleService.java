package com.carsil.userapi.service;

import com.carsil.userapi.model.Module;
import com.carsil.userapi.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Module create(Module module) {
        return moduleRepository.save(module);
    }

    public Optional<Module> update(Long id, Module input) {
        return moduleRepository.findById(id).map(existing -> {
            existing.setDescription(input.getDescription());
            existing.setName(input.getName());
            existing.setRemainingTime(input.getRemainingTime());
            return moduleRepository.save(existing);
        });
    }
}