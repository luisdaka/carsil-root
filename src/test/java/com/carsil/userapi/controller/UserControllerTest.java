package com.carsil.userapi.controller;

import com.carsil.userapi.model.User;
import com.carsil.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "test", roles = {"USER"})
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void getUsers_returnsList() throws Exception {
        User u = new User();
        u.setId(1L); u.setName("luis"); u.setEmail("l@test.com"); u.setPassword("x");
        Mockito.when(userService.getAll()).thenReturn(List.of(u));

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("luis")));
    }

    @Test
    void createUser_returnsSaved() throws Exception {
        User ret = new User();
        ret.setId(10L); ret.setName("luis"); ret.setEmail("l@test.com"); ret.setPassword("ENC");
        Mockito.when(userService.create(any(User.class))).thenReturn(ret);

        mvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"luis\",\"email\":\"l@test.com\",\"password\":\"raw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("luis")));
    }

    @Test
    void updateUser_callsCreate_andReturnsSaved() throws Exception {
        User ret = new User();
        ret.setId(5L); ret.setName("nuevo"); ret.setEmail("n@test.com"); ret.setPassword("ENC");
        Mockito.when(userService.create(any(User.class))).thenReturn(ret);

        mvc.perform(put("/api/users/5")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"nuevo\",\"email\":\"n@test.com\",\"password\":\"raw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("nuevo")));
    }

    @Test
    void deleteUser_callsService() throws Exception {
        mvc.perform(delete("/api/users/9")
                        .with(csrf()))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(eq(9L));
    }
}
