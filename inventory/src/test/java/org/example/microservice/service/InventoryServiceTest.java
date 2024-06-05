package org.example.microservice.service;

import org.example.microservice.exception.ProductNotFoundException;
import org.example.microservice.model.Product;
import org.example.microservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;

    @BeforeEach
    public void setup() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setUpc("1234567890");
        product.setWholesalePrice(new BigDecimal(100.00));
        product.setRetailPrice(new BigDecimal(150.0));
        product.setQuantity(10);
    }

    @Test
    public void testAddProduct_NewProduct() {
        when(inventoryRepository.findProductByUpc(product.getUpc())).thenReturn(null);
        when(inventoryRepository.save(product)).thenReturn(product);

        Product savedProduct = inventoryService.addProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        verify(inventoryRepository, times(1)).findProductByUpc(product.getUpc());
        verify(inventoryRepository, times(1)).save(product);
    }

    @Test
    public void testAddProduct_ExistingProduct() {
        when(inventoryRepository.findProductByUpc(product.getUpc())).thenReturn(product);
        when(inventoryRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(inventoryRepository.save(product)).thenReturn(product);

        Product updatedProduct = inventoryService.addProduct(product);

        assertNotNull(updatedProduct);
        assertEquals("Test Product", updatedProduct.getName());
        verify(inventoryRepository, times(1)).findProductByUpc(product.getUpc());
        verify(inventoryRepository, times(1)).save(product);
    }

    @Test
    public void testGetProductByUpc_ProductFound() {
        when(inventoryRepository.findProductByUpc(product.getUpc())).thenReturn(product);

        Product foundProduct = inventoryService.getProductByUpc(product.getUpc());

        assertNotNull(foundProduct);
        assertEquals("Test Product", foundProduct.getName());
        verify(inventoryRepository, times(1)).findProductByUpc(product.getUpc());
    }

    @Test
    public void testGetProductByUpc_ProductNotFound() {
        when(inventoryRepository.findProductByUpc(product.getUpc())).thenReturn(null);

        Product foundProduct = inventoryService.getProductByUpc(product.getUpc());

        assertNull(foundProduct);
        verify(inventoryRepository, times(1)).findProductByUpc(product.getUpc());
    }

    @Test
    public void testGetProductById_ProductFound() {
        when(inventoryRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product foundProduct = inventoryService.getProductById(product.getId());

        assertNotNull(foundProduct);
        assertEquals("Test Product", foundProduct.getName());
        verify(inventoryRepository, times(1)).findById(product.getId());
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        when(inventoryRepository.findById(product.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.getProductById(product.getId());
        });

        assertEquals("Product with ID " + product.getId() + " not found", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(product.getId());
    }

    @Test
    public void testUpdateProduct_ProductFound() {
        when(inventoryRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(inventoryRepository.save(product)).thenReturn(product);

        Product updatedProduct = inventoryService.updateProduct(product.getId(), product);

        assertNotNull(updatedProduct);
        assertEquals("Test Product", updatedProduct.getName());
        verify(inventoryRepository, times(1)).findById(product.getId());
        verify(inventoryRepository, times(1)).save(product);
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        when(inventoryRepository.findById(product.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.updateProduct(product.getId(), product);
        });

        assertEquals("Product with ID " + product.getId() + " not found", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(product.getId());
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(inventoryRepository.findAll()).thenReturn(products);

        List<Product> allProducts = inventoryService.getAllProducts();

        assertNotNull(allProducts);
        assertFalse(allProducts.isEmpty());
        assertEquals(1, allProducts.size());
        verify(inventoryRepository, times(1)).findAll();
    }
}
