package org.example.microservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservice.exception.ProductNotFoundException;
import org.example.microservice.model.Product;
import org.example.microservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * Adds a new product to the inventory. If a product with the same UPC exists, it updates the existing product.
     *
     * @param product the product to be added or updated
     * @return the added or updated product
     */
    public Product addProduct(Product product) {
        log.info("Adding product with UPC: {}", product.getUpc());
        Product existingProduct = getProductByUpc(product.getUpc());
        if (existingProduct != null) {
            log.info("Product with UPC: {} exists, updating product", product.getUpc());
            return updateProduct(existingProduct.getId(), product);
        } else {
            log.info("Product with UPC: {} does not exist, saving new product", product.getUpc());
            return inventoryRepository.save(product);
        }
    }

    /**
     * Retrieves a product by its UPC.
     *
     * @param upc the UPC of the product to be retrieved
     * @return the product with the specified UPC
     */
    public Product getProductByUpc(String upc) {
        log.info("Retrieving product by UPC: {}", upc);
        return inventoryRepository.findProductByUpc(upc);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to be retrieved
     * @return the product with the specified ID
     * @throws ProductNotFoundException if the product with the specified ID is not found
     */
    public Product getProductById(Long id) {
        log.info("Retrieving product by ID: {}", id);
        return inventoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new ProductNotFoundException("Product with ID " + id + " not found");
                });
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id the ID of the product to be updated
     * @param product the product details to be updated
     * @return the updated product
     * @throws ProductNotFoundException if the product with the specified ID is not found
     */
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = inventoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new ProductNotFoundException("Product with ID " + id + " not found");
                });

        existingProduct.setName(product.getName());
        existingProduct.setUpc(product.getUpc());
        existingProduct.setWholesalePrice(product.getWholesalePrice());
        existingProduct.setRetailPrice(product.getRetailPrice());
        existingProduct.setQuantity(product.getQuantity());
        log.info("Product with ID: {} updated successfully", id);
        return inventoryRepository.save(existingProduct);
    }

    /**
     * Retrieves all products in the inventory.
     *
     * @return a list of all products
     */
    public List<Product> getAllProducts() {
        log.info("Retrieving all products");
        return inventoryRepository.findAll();
    }
}
