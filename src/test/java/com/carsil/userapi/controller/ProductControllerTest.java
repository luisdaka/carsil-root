package com.carsil.userapi.controller;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@WithMockUser(username = "test", roles = {"USER"})
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getProducts_returnsList_whenCalled() throws Exception {
        Product p1 = new Product();
        p1.setId(10L);
        p1.setReference("REF10");
        Product p2 = new Product();
        p2.setId(20L);
        p2.setReference("REF20");

        Mockito.when(productService.getAll()).thenReturn(List.of(p1, p2));

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reference", is("REF10")))
                .andExpect(jsonPath("$[1].reference", is("REF20")));
    }

    @Test
    void createProduct_callsService_andReturnsSaved() throws Exception {
        Product productToSave = new Product();
        productToSave.setReference("NEW_REF");
        productToSave.setQuantity(1);
        productToSave.setPrice(100.0);
        productToSave.setAssignedDate(LocalDate.now());
        productToSave.setPlantEntryDate(LocalDate.now());
        productToSave.setBrand("BrandX");
        productToSave.setOp("OP123");
        productToSave.setCampaign("CAMP1");
        productToSave.setType("TypeA");
        productToSave.setSize("SizeB");

        Product savedProduct = new Product();
        savedProduct.setId(15L);
        savedProduct.setReference("NEW_REF");

        Mockito.when(productService.create(any(Product.class))).thenReturn(savedProduct);

        mvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(15)))
                .andExpect(jsonPath("$.reference", is("NEW_REF")));
    }

    @Test
    void updateProduct_callsService_andReturnsUpdated() throws Exception {
        Product productToUpdate = new Product();
        productToUpdate.setReference("UPDATED_REF");
        productToUpdate.setQuantity(5);
        productToUpdate.setPrice(250.0);
        productToUpdate.setAssignedDate(LocalDate.now());
        productToUpdate.setPlantEntryDate(LocalDate.now());
        productToUpdate.setBrand("BrandY");
        productToUpdate.setOp("OP456");
        productToUpdate.setCampaign("CAMP2");
        productToUpdate.setType("TypeB");
        productToUpdate.setSize("SizeC");

        Product updatedProduct = new Product();
        updatedProduct.setId(12L);
        updatedProduct.setReference("UPDATED_REF");

        Mockito.when(productService.update(any(Product.class), eq(12L))).thenReturn(updatedProduct);

        mvc.perform(put("/api/products/12")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(12)))
                .andExpect(jsonPath("$.reference", is("UPDATED_REF")));
    }

    @Test
    void deleteProduct_callsService() throws Exception {
        Mockito.doNothing().when(productService).delete(5L);

        mvc.perform(delete("/api/products/5")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}