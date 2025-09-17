package com.carsil.userapi.service;

import com.carsil.userapi.exception.ResourceNotFoundException;
import com.carsil.userapi.model.User;
import com.carsil.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User create(User u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepository.save(u);
    }

    public User update(Long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede actualizar. Usuario con ID " + id + " no existe.");
        }
        // Si envías nueva contraseña, la encripta
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // conserva la actual
            user.setPassword(userRepository.findById(id).get().getPassword());
        }
        user.setId(id);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Usuario con ID " + id + " no existe.");
        }
        userRepository.deleteById(id);
    }

    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado."));
    }

    public User getByNameOrThrow(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con nombre '" + name + "' no encontrado."));
    }

    public boolean validateLogin(String name, String rawPassword) {
        return userRepository.findByName(name)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }
}