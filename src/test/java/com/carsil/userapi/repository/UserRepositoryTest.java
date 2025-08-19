package com.carsil.userapi.repository;

import com.carsil.userapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByName_returnsUser_whenExists() {
        User u = new User();
        u.setName("luis");
        u.setEmail("l@test.com");
        u.setPassword("x");
        userRepository.save(u); // JpaRepository.save(...):contentReference[oaicite:12]{index=12}

        Optional<User> found = userRepository.findByName("luis"); // método declarado en tu repo:contentReference[oaicite:13]{index=13}
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("l@test.com");
    }

    @Test
    void findByName_returnsEmpty_whenNotExists() {
        assertThat(userRepository.findByName("ghost")).isEmpty();
    }
}
