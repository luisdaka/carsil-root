package com.carsil.userapi.service;

import com.carsil.userapi.model.User;
import com.carsil.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public boolean validateLogin(String name, String rawPassword) {
        Optional<User> user = userRepository.findByName(name);
        return user.isPresent() &&
                passwordEncoder.matches(rawPassword, user.get().getPassword());
    }
}

