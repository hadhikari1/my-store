package org.example.microservice.repository;

import org.example.microservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Product, Long> {
    public Product findProductByUpc(String upc);
    public Product findProductById(Long id);
}