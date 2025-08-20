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

@ExtendWith(MockitoExtension.class) //1
class ProductServiceTest {

    @Mock //2
    private ProductRepository productRepository;

    @InjectMocks //3
    private ProductService productService;

    private Product testProduct;

    @BeforeEach //4
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
        testProduct.setSize("SizeB");
    }

    @Test
    void getAll_returnsListOfProducts() {
        // Simulación: Cuando se llama a findAll(), devolver una lista con nuestro producto de prueba
        when(productRepository.findAll()).thenReturn(Collections.singletonList(testProduct));

        // Ejecutar el método a probar
        List<Product> products = productService.getAll();

        // Verificaciones
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        assertEquals(testProduct.getReference(), products.get(0).getReference());
        // Verificar que el método del repositorio se llamó exactamente una vez
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void create_returnsSavedProduct() {
        // Simulación: Cuando se llama a save() con cualquier objeto Product, devolver el producto de prueba
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Ejecutar el método a probar
        Product createdProduct = productService.create(new Product());

        // Verificaciones
        assertNotNull(createdProduct);
        assertEquals(testProduct.getReference(), createdProduct.getReference());
        // Verificar que el método del repositorio se llamó exactamente una vez
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        // Ejecutar el método a probar
        productService.delete(1L);

        // Verificar que el método del repositorio se llamó exactamente una vez con el ID correcto
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void update_whenProductExists_returnsUpdatedProduct() {
        Product updatedData = new Product();
        updatedData.setReference("UPDATED_REF");
        updatedData.setPrice(99.99);

        // Simulación 1: Cuando se busca por el ID, devuelve el producto de prueba
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        // Simulación 2: Cuando se guarda cualquier Product, devuelve un nuevo objeto (podría ser testProduct con los datos actualizados)
        when(productRepository.save(any(Product.class))).thenReturn(updatedData);

        // Ejecutar el método a probar
        Product result = productService.update(updatedData, 1L);

        // Verificaciones
        assertNotNull(result);
        assertEquals(updatedData.getReference(), result.getReference());
        assertEquals(updatedData.getPrice(), result.getPrice());

        // Verificar las llamadas al repositorio
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void update_whenProductDoesNotExist_throwsException() {
        // Simulación: Cuando se busca por un ID, no devuelve nada (Optional.empty)
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificar que el método lanza una excepción del tipo RuntimeException
        assertThrows(RuntimeException.class, () -> productService.update(new Product(), 2L));

        // Verificar que el método save() nunca se llamó
        verify(productRepository, never()).save(any(Product.class));
    }
}