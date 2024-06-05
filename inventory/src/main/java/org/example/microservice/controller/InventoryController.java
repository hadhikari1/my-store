package org.example.microservice.controller;

import lombok.AllArgsConstructor;
import org.example.microservice.model.Product;
import org.example.microservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventory")
@CrossOrigin("http://localhost:3000/")
public class InventoryController {
    @Autowired
    private final InventoryService inventoryService;

    @PostMapping("add-product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product addedProduct = inventoryService.addProduct(product);
        return new ResponseEntity<>(addedProduct, HttpStatus.CREATED);
    }

    @GetMapping("products")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = inventoryService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = inventoryService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}
