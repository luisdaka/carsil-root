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
            throw new IllegalArgumentException("La contraseña es obligatoria y no puede estar vacía.");
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepository.save(u);
    }

    public User update(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            // Verifica si hay una nueva contraseña
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // Actualiza solo los campos permitidos
            if (user.getName() != null && !user.getName().isBlank()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                existingUser.setEmail(user.getEmail());
            }
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException(
                "No se pudo actualizar. El usuario con ID " + id + " no existe en la base de datos. " +
                        "Verifica que el ID sea correcto o que el usuario no haya sido eliminado previamente."
        ));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No se pudo eliminar. El usuario con ID " + id + " no existe en la base de datos. " +
                            "Es posible que el usuario ya haya sido eliminado o nunca se haya registrado."
            );
        }
        userRepository.deleteById(id);
    }

    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró ningún usuario con el ID " + id + ". " +
                                "Verifica que el ID ingresado sea correcto."
                ));
    }

    public User getByNameOrThrow(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró ningún usuario con el nombre '" + name + "'. " +
                                "Verifica que el nombre esté escrito correctamente o que el usuario exista en el sistema."
                ));
    }

    public boolean validateLogin(String name, String rawPassword) {
        return userRepository.findByName(name)
                .map(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        throw new IllegalArgumentException(
                                "La contraseña ingresada para el usuario '" + name + "' es incorrecta. " +
                                        "Verifica e inténtalo nuevamente."
                        );
                    }
                    return true;
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró ningún usuario con el nombre '" + name + "'. " +
                                "Asegúrate de que el usuario esté registrado antes de intentar iniciar sesión."
                ));
    }
}