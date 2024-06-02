package org.example.microservice.service;

import lombok.RequiredArgsConstructor;
import org.example.microservice.model.Product;
import org.example.microservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Product addProduct(Product product) {
        try {
            // look up product by upc
            Product existingProduct = getProductByUpc(product.getUpc());
            // if product exist update the product
            // else save product
            if (existingProduct != null) {
                return updateProduct(existingProduct.getId(), product);
            } else {
                return inventoryRepository.save(product);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Product getProductByUpc(String upc) {
        try {
            return inventoryRepository.findProductByUpc(upc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Product updateProduct(Long id, Product product) {
        try {
            Product existingProduct = inventoryRepository.findProductById(id);
            if (existingProduct != null) {
                existingProduct.setName(product.getName());
                existingProduct.setUpc(product.getUpc());
                existingProduct.setWholesalePrice(product.getWholesalePrice());
                existingProduct.setRetailPrice(product.getRetailPrice());
                existingProduct.setQuantity(product.getQuantity());
                return inventoryRepository.save(existingProduct);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
