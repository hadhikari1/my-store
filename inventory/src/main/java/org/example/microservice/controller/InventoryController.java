package org.example.microservice.controller;

import lombok.AllArgsConstructor;
import org.example.microservice.model.Product;
import org.example.microservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private final InventoryService inventoryService;

    @PostMapping("add-product")
    public Product addProduct(@RequestBody Product product) {
        return inventoryService.addProduct(product);
    }

}
