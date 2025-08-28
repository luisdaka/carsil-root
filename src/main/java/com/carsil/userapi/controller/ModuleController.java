package com.carsil.userapi.controller;

import com.carsil.userapi.model.Module;
import com.carsil.userapi.service.ModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping
    public List<Module> getAll() {
        return moduleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Module> findById(@PathVariable Long id) {
        return moduleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name")
    public List<Module> findByName(@RequestParam String name) {
        return moduleService.findByName(name);
    }

    @PostMapping
    public Module create(@RequestBody Module module) {
        return moduleService.create(module);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Module> update(@PathVariable Long id, @RequestBody Module input) {
        return moduleService.update(id, input)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}