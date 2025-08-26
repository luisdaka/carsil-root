package com.carsil.userapi.service;

import com.carsil.userapi.model.Module;
import com.carsil.userapi.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> getAll() {
        return moduleRepository.findAll();
    }

    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }

    public List<Module> findByName(String name) {
        return moduleRepository.findByNameContainingIgnoreCase(name);
    }
}