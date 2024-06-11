package org.example.microservice.controller;

import lombok.AllArgsConstructor;
import org.example.microservice.dto.ShoppingCartDto;
import org.example.microservice.model.Product;
import org.example.microservice.model.ShoppingCart;
import org.example.microservice.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {
    @Autowired
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<ShoppingCart> addToCart(@RequestBody ShoppingCartDto shoppingCartDto) {
        ShoppingCart shoppingCart = shoppingCartService.addToCart(shoppingCartDto);
        return ResponseEntity.ok(shoppingCart);
    }

    @PostMapping("/add-total")
    public ResponseEntity<BigDecimal> getTotal(@RequestBody List<Long> ids) {
        BigDecimal total = shoppingCartService.getTotal(ids);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody List<Long> ids) {
        List<Object> result = shoppingCartService.checkout(ids);
        List<Product> products = (List<Product>) result.get(0);
        List<String> messages = (List<String>) result.get(1);

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("messages", messages);

        return ResponseEntity.ok(response);
    }
}
