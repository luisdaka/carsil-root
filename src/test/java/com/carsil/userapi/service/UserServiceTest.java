package com.carsil.userapi.service;

import com.carsil.userapi.model.User;
import com.carsil.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService();
        // Inyectamos los mocks vía reflexión por simplicidad (tu servicio usa @Autowired)
        // Alternativa: constructor + @RequiredArgsConstructor en producción.
        try {
            var repoField = UserService.class.getDeclaredField("userRepository");
            repoField.setAccessible(true);
            repoField.set(userService, userRepository);

            var encField = UserService.class.getDeclaredField("passwordEncoder");
            encField.setAccessible(true);
            encField.set(userService, passwordEncoder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));
        assertThat(userService.getAll()).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void create_encodesPassword_andSaves() {
        User u = new User();
        u.setName("luis");
        u.setEmail("luis@test.com");
        u.setPassword("raw");

        when(passwordEncoder.encode("raw")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.create(u);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();

        assertThat(toSave.getPassword()).isEqualTo("ENCODED");
        assertThat(saved.getPassword()).isEqualTo("ENCODED");
        verify(passwordEncoder).encode("raw");
    }

    @Test
    void delete_callsRepository() {
        userService.delete(10L);
        verify(userRepository).deleteById(10L);
    }

    @Test
    void validateLogin_returnsTrue_whenPasswordMatches() {
        User u = new User();
        u.setName("luis");
        u.setPassword("HASH");

        when(userRepository.findByName("luis")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secret", "HASH")).thenReturn(true);

        boolean ok = userService.validateLogin("luis", "secret");
        assertThat(ok).isTrue();

        verify(userRepository).findByName("luis");
    }

    @Test
    void validateLogin_returnsFalse_whenUserMissingOrPasswordMismatch() {
        when(userRepository.findByName("nope")).thenReturn(Optional.empty());
        assertThat(userService.validateLogin("nope", "x")).isFalse();

        User u = new User();
        u.setName("luis");
        u.setPassword("HASH");
        when(userRepository.findByName("luis")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("bad", "HASH")).thenReturn(false);
        assertThat(userService.validateLogin("luis", "bad")).isFalse();
    }
}
