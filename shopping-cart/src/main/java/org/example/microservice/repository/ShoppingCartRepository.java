package org.example.microservice.repository;

import org.example.microservice.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    public List<ShoppingCart> findByIsCheckoutFalseAndQuantityGreaterThan(int quantity);
}
