package com.carsil.userapi.service;

import com.carsil.userapi.model.Product;
import com.carsil.userapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setPrice(150.0);
        testProduct.setQuantity(10);
        testProduct.setReference("REF123");
        testProduct.setBrand("BrandX");
        testProduct.setAssignedDate(LocalDate.now());
        testProduct.setPlantEntryDate(LocalDate.now());
        testProduct.setOp("OP456");
        testProduct.setCampaign("C789");
        testProduct.setType("TypeA");
    }


    @Test
    void getAll_returnsListOfProducts() {

        when(productRepository.findAll()).thenReturn(Collections.singletonList(testProduct));
        List<Product> products = productService.getAll();
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        assertEquals(testProduct.getReference(), products.get(0).getReference());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void create_returnsSavedProduct() {

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        Product createdProduct = productService.create(new Product());
        assertNotNull(createdProduct);
        assertEquals(testProduct.getReference(), createdProduct.getReference());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        productService.delete(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void update_whenProductExists_returnsUpdatedProduct() {
        Product updatedData = new Product();
        updatedData.setReference("UPDATED_REF");
        updatedData.setPrice(99.99);


        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedData);

        Product result = productService.update(updatedData, 1L);

        assertNotNull(result);
        assertEquals(updatedData.getReference(), result.getReference());
        assertEquals(updatedData.getPrice(), result.getPrice());


        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void update_whenProductDoesNotExist_throwsException() {

        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.update(new Product(), 2L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void search_returnsMultipleProducts_whenQueryMatchesDifferentFields() {
        Product p1 = new Product();
        p1.setId(401L);
        p1.setReference("NIKE-REF");
        p1.setBrand("BrandZ");
        p1.setOp("OP-NIKE");
        p1.setCampaign("CAMPZ");
        p1.setAssignedDate(LocalDate.now());
        p1.setPlantEntryDate(LocalDate.now());
        p1.setPrice(150.0);
        p1.setQuantity(3);
        p1.setType("TypeX");

        Product p2 = new Product();
        p2.setId(402L);
        p2.setReference("REF123");
        p2.setBrand("Nike");
        p2.setOp("OP123");
        p2.setCampaign("CAMPY");
        p2.setAssignedDate(LocalDate.now());
        p2.setPlantEntryDate(LocalDate.now());
        p2.setPrice(250.0);
        p2.setQuantity(7);
        p2.setType("TypeY");

        when(productRepository.search("nike")).thenReturn(List.of(p1, p2));

        List<Product> results = productService.search("nike");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> "Nike".equals(p.getBrand())));
        assertTrue(results.stream().anyMatch(p -> p.getReference().contains("NIKE")));
    }
}