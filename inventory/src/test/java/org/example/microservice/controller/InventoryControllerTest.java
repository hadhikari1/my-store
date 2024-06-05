package org.example.microservice.controller;

import org.example.microservice.exception.ProductNotFoundException;
import org.example.microservice.model.Product;
import org.example.microservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Product product;

    @BeforeEach
    public void setup() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setUpc("1234567890");
        product.setWholesalePrice(new BigDecimal(100.0));
        product.setRetailPrice(new BigDecimal(150.0));
        product.setQuantity(10);
    }

    @Test
    public void testAddProduct() {
        when(inventoryService.addProduct(product)).thenReturn(product);

        ResponseEntity<Product> responseEntity = inventoryController.addProduct(product);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(product, responseEntity.getBody());
        verify(inventoryService, times(1)).addProduct(product);
    }

    @Test
    public void testGetProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(inventoryService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<Product>> responseEntity = inventoryController.getProducts();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(products, responseEntity.getBody());
        verify(inventoryService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductById_ProductFound() {
        when(inventoryService.getProductById(product.getId())).thenReturn(product);

        ResponseEntity<Product> responseEntity = inventoryController.getProductById(product.getId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(product, responseEntity.getBody());
        verify(inventoryService, times(1)).getProductById(product.getId());
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        when(inventoryService.getProductById(product.getId())).thenThrow(new ProductNotFoundException("Product with ID " + product.getId() + " not found"));

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            inventoryController.getProductById(product.getId());
        });

        assertEquals("Product with ID " + product.getId() + " not found", exception.getMessage());
        verify(inventoryService, times(1)).getProductById(product.getId());
    }
}
