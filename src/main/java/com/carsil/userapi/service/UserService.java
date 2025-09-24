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
        if (u.getPassword() == null || u.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria y no puede estar vacía."
            );
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepository.save(u);
    }

    public User update(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (user.getName() != null && !user.getName().isBlank()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                existingUser.setEmail(user.getEmail());
            }
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con ID " + id + " no encontrado para actualización."
        ));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Usuario con ID " + id + " no existe en la base de datos."
            );
        }
        userRepository.deleteById(id);
    }

    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con ID " + id + " no fue encontrado."
                ));
    }

    public User getByNameOrThrow(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con nombre '" + name + "' no existe en el sistema."
                ));
    }

    public boolean validateLogin(String name, String rawPassword) {
        return userRepository.findByName(name)
                .map(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        throw new IllegalArgumentException(
                                "La contraseña ingresada es incorrecta para el usuario '" + name + "'."
                        );
                    }
                    return true;
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un usuario registrado con el nombre '" + name + "'."
                ));
    }
}

