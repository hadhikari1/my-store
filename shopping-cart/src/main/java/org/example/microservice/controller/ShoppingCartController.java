package org.example.microservice.controller;

import lombok.AllArgsConstructor;
import org.example.microservice.dto.ShoppingCartDto;
import org.example.microservice.model.Product;
import org.example.microservice.model.ShoppingCart;
import org.example.microservice.repository.ShoppingCartRepository;
import org.example.microservice.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {
    @Autowired
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/add-to-cart")
    public ShoppingCart addToCart(@RequestBody ShoppingCartDto shoppingCartDto){
        return shoppingCartService.addToCart(shoppingCartDto);

    }

    @PostMapping("/add-total")
    public BigDecimal getTotal(@RequestBody List<Long> ids){
        return shoppingCartService.getTotal(ids);
    }

    @PostMapping("/checkout")
    public List<Product> checkout(@RequestBody List<Long> ids){
        return shoppingCartService.checkout(ids);
    }


}
