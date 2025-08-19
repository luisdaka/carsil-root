package com.carsil.userapi.controller;

import com.carsil.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@WithMockUser(username = "test", roles = {"USER"})
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void login_returns200_whenValid() throws Exception {
        Mockito.when(userService.validateLogin("luis","secret")).thenReturn(true);

        mvc.perform(post("/api/auth/login")
                        .with(csrf()) // evita 403 por CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"luis\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Login successful")));
    }

    @Test
    void login_returns401_whenInvalid() throws Exception {
        Mockito.when(userService.validateLogin("luis","bad")).thenReturn(false);

        mvc.perform(post("/api/auth/login")
                        .with(csrf()) // evita 403 por CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"luis\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Invalid credentials")));
    }
}
