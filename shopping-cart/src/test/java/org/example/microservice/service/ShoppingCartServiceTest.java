package org.example.microservice.service;

import org.example.microservice.dto.ShoppingCartDto;
import org.example.microservice.exception.InsufficientQuantityException;
import org.example.microservice.exception.ProductNotFoundException;
import org.example.microservice.exception.ShoppingCartNotFoundException;
import org.example.microservice.model.Product;
import org.example.microservice.model.ShoppingCart;
import org.example.microservice.repository.InventoryRepository;
import org.example.microservice.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddToCart_ProductNotFound() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        ShoppingCartDto dto = new ShoppingCartDto(1L, 1L, 5);

        assertThrows(ProductNotFoundException.class, () -> shoppingCartService.addToCart(dto));
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddToCart_InsufficientQuantity() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setQuantity(2);
        product.setRetailPrice(BigDecimal.valueOf(10));

        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ShoppingCartDto dto = new ShoppingCartDto(1L, 1L, 5);

        assertThrows(InsufficientQuantityException.class, () -> shoppingCartService.addToCart(dto));
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddToCart_ShoppingCartNotFound() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setQuantity(10);
        product.setRetailPrice(BigDecimal.valueOf(10));

        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        ShoppingCartDto dto = new ShoppingCartDto(1L, 1L, 5);

        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.addToCart(dto));
        verify(inventoryRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddToCart_CreateNewShoppingCart() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setQuantity(10);
        product.setRetailPrice(BigDecimal.valueOf(10));

        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ShoppingCartDto dto = new ShoppingCartDto(0L, 1L, 5);

        ShoppingCart newShoppingCart = new ShoppingCart();
        newShoppingCart.setProduct(product);
        newShoppingCart.setQuantity(5);
        newShoppingCart.setTotalAmount(BigDecimal.valueOf(50));
        newShoppingCart.setCheckout(false);

        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(newShoppingCart);

        ShoppingCart result = shoppingCartService.addToCart(dto);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(BigDecimal.valueOf(50), result.getTotalAmount());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    public void testAddToCart_UpdateExistingShoppingCart() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setQuantity(10);
        product.setRetailPrice(BigDecimal.valueOf(10));

        ShoppingCart existingCart = new ShoppingCart();
        existingCart.setId(1L);
        existingCart.setProduct(product);
        existingCart.setQuantity(3);
        existingCart.setTotalAmount(BigDecimal.valueOf(30));
        existingCart.setCheckout(false);

        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.of(existingCart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(existingCart);

        ShoppingCartDto dto = new ShoppingCartDto(1L, 1L, 5);

        ShoppingCart result = shoppingCartService.addToCart(dto);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(BigDecimal.valueOf(50), result.getTotalAmount());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    public void testGetTotal_ShoppingCartNotFound() {
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        List<Long> shoppingCartIds = Arrays.asList(1L, 2L, 3L);

        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.getTotal(shoppingCartIds));
        verify(shoppingCartRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetTotal_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setRetailPrice(BigDecimal.valueOf(10));

        ShoppingCart cart1 = new ShoppingCart();
        cart1.setId(1L);
        cart1.setProduct(product);
        cart1.setQuantity(2);
        cart1.setCheckout(false);

        ShoppingCart cart2 = new ShoppingCart();
        cart2.setId(2L);
        cart2.setProduct(product);
        cart2.setQuantity(3);
        cart2.setCheckout(false);

        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart1));
        when(shoppingCartRepository.findById(2L)).thenReturn(Optional.of(cart2));

        List<Long> shoppingCartIds = Arrays.asList(1L, 2L);
        BigDecimal total = shoppingCartService.getTotal(shoppingCartIds);

        assertEquals(BigDecimal.valueOf(50), total);
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findById(2L);
    }

    @Test
    public void testCheckout_ShoppingCartNotFound() {
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        List<Long> shoppingCartIds = Arrays.asList(1L, 2L);

        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.checkout(shoppingCartIds));
        verify(shoppingCartRepository, times(1)).findById(1L);
    }

    @Test
    public void testCheckout_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setQuantity(10);
        product.setRetailPrice(BigDecimal.valueOf(10));

        ShoppingCart cart1 = new ShoppingCart();
        cart1.setId(1L);
        cart1.setProduct(product);
        cart1.setQuantity(2);
        cart1.setCheckout(false);

        ShoppingCart cart2 = new ShoppingCart();
        cart2.setId(2L);
        cart2.setProduct(product);
        cart2.setQuantity(3);
        cart2.setCheckout(false);

        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart1));
        when(shoppingCartRepository.findById(2L)).thenReturn(Optional.of(cart2));
        when(inventoryRepository.save(any(Product.class))).thenReturn(product);

        List<Long> shoppingCartIds = Arrays.asList(1L, 2L);
        List<Object> result = shoppingCartService.checkout(shoppingCartIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, product.getQuantity());
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findById(2L);
        verify(inventoryRepository, times(2)).save(any(Product.class));
    }
}
