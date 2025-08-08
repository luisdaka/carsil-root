
package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);//esto no en producto. solo lo de arriba
}
