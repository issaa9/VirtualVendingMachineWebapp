package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {  //set up a sample product for each test
        sampleProduct = new Product();
        sampleProduct.setId("A1");
        sampleProduct.setStock(10);
    }

    @Test  //test to find a product by its ID
    void testGetProductById_validId_returnsProduct() {
        when(productRepo.findById("A1")).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getProductById("A1");

        assertNotNull(result);
        assertEquals("A1", result.getId());
        verify(productRepo, times(1)).findById("A1");
    }

    @Test //test to update/increase a product's stock
    void testUpdateStock_increaseStock_success() {
        when(productRepo.findById("A1")).thenReturn(Optional.of(sampleProduct));

        productService.updateStock("A1", 5);
        assertEquals(15, sampleProduct.getStock());
    }

    @Test //test to update/decrease a product's stock
    void testUpdateStock_decreaseStock_success() {
        when(productRepo.findById("A1")).thenReturn(Optional.of(sampleProduct));

        productService.updateStock("A1", -3);
        assertEquals(7, sampleProduct.getStock());
    }

    @Test  //test to update/decrease a product's stock to a negative value to check it is handled
    void testUpdateStock_negativeStock_throwsException() {
        when(productRepo.findById("A1")).thenReturn(Optional.of(sampleProduct));

        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStock("A1", -20);
        });
    }
}
