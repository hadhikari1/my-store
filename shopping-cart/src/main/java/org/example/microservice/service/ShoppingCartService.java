package org.example.microservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.microservice.dto.ShoppingCartDto;
import org.example.microservice.exception.AlreadyCheckedOutException;
import org.example.microservice.exception.InsufficientQuantityException;
import org.example.microservice.exception.ProductNotFoundException;
import org.example.microservice.exception.ShoppingCartNotFoundException;
import org.example.microservice.model.Product;
import org.example.microservice.model.ShoppingCart;
import org.example.microservice.repository.InventoryRepository;
import org.example.microservice.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {
    private final InventoryRepository inventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    /**
     * Adds a product to a shopping cart.
     *
     * @param shoppingCartDto the shopping cart data transfer object containing product and quantity information
     * @return the updated or newly created shopping cart
     * @throws ProductNotFoundException if the product is not found
     * @throws InsufficientQuantityException if the requested quantity exceeds available quantity
     * @throws ShoppingCartNotFoundException if the shopping cart is not found
     */
    public ShoppingCart addToCart(ShoppingCartDto shoppingCartDto) {
        log.info("Adding product with ID {} to shopping cart with ID {}", shoppingCartDto.getProductId(), shoppingCartDto.getShoppingCartId());
        Product product = inventoryRepository.findById(shoppingCartDto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found", shoppingCartDto.getProductId());
                    return new ProductNotFoundException("Product not found");
                });

        if (!addItems(shoppingCartDto.getPurchaseQuantity(), product.getQuantity())) {
            log.error("Insufficient quantity for product: {}. Requested: {}, Available: {}", product.getName(),
                    shoppingCartDto.getPurchaseQuantity(), product.getQuantity());
            throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName());
        }

        BigDecimal total = product.getRetailPrice().multiply(new BigDecimal(shoppingCartDto.getPurchaseQuantity()));

        if (shoppingCartDto.getShoppingCartId() > 0) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartDto.getShoppingCartId())
                    .orElseThrow(() -> {
                        log.error("Shopping cart with ID {} not found", shoppingCartDto.getShoppingCartId());
                        return new ShoppingCartNotFoundException("Shopping cart not found");
                    });
            if (!shoppingCart.isCheckout() && shoppingCart.getQuantity() > 0) {
                log.info("Updating quantity for shopping cart with ID {}", shoppingCartDto.getShoppingCartId());
                shoppingCart.setTotalAmount(total);
                shoppingCart.setQuantity(shoppingCartDto.getPurchaseQuantity());
                return shoppingCartRepository.save(shoppingCart);
            }else {
                throw new AlreadyCheckedOutException("Failed to add to cart, this item is already checked out.");
            }
        }

        log.info("Creating new shopping cart for product with ID {}", shoppingCartDto.getProductId());
        ShoppingCart newShoppingCart = ShoppingCart
                .builder()
                .product(product)
                .totalAmount(total)
                .quantity(shoppingCartDto.getPurchaseQuantity())
                .isCheckout(false)
                .build();
        return shoppingCartRepository.save(newShoppingCart);
    }

    public List<ShoppingCart> getAllNonCheckedOutItems(){
        return shoppingCartRepository.findByIsCheckoutFalseAndQuantityGreaterThan(0);
    }

    /**
     * Checks if the purchase quantity is less than or equal to the available product quantity.
     *
     * @param purchaseQuantity the quantity to be purchased
     * @param productQuantity the available quantity of the product
     * @return true if the purchase quantity is less than or equal to the product quantity, false otherwise
     */
    public boolean addItems(int purchaseQuantity, int productQuantity) {
        log.info("Checking if purchase quantity {} is less than or equal to product quantity {}", purchaseQuantity, productQuantity);
        return purchaseQuantity <= productQuantity;
    }

    /**
     * Calculates the total amount for a list of shopping cart IDs.
     *
     * @param shoppingCartIds the list of shopping cart IDs
     * @return the total amount
     * @throws ShoppingCartNotFoundException if any shopping cart is not found
     */
    public BigDecimal getTotal(List<Long> shoppingCartIds) {
        log.info("Calculating total for shopping cart IDs: {}", shoppingCartIds);
        BigDecimal total = BigDecimal.ZERO;

        for (Long id : shoppingCartIds) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Shopping cart with ID {} not found", id);
                        return new ShoppingCartNotFoundException("Shopping cart not found");
                    });

            if (!shoppingCart.isCheckout()) {
                Product product = shoppingCart.getProduct();
                total = total.add(product.getRetailPrice().multiply(new BigDecimal(shoppingCart.getQuantity())));
                log.info("Added product price {} to total. Current total: {}", product.getRetailPrice().multiply(new BigDecimal(shoppingCart.getQuantity())), total);
            }
        }
        log.info("Final total for shopping cart IDs {}: {}", shoppingCartIds, total);
        return total;
    }

    /**
     * Checks out a list of shopping cart IDs, updating product quantities and marking carts as checked out.
     *
     * @param shoppingCartIds the list of shopping cart IDs to check out
     * @return a list containing the updated products and any messages related to the checkout process
     * @throws ShoppingCartNotFoundException if any shopping cart is not found
     */
    public List<Object> checkout(List<Long> shoppingCartIds) {
        log.info("Checking out shopping cart IDs: {}", shoppingCartIds);
        List<Product> products = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        for (Long id : shoppingCartIds) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Shopping cart with ID {} not found", id);
                        return new ShoppingCartNotFoundException("Shopping cart not found");
                    });

            if (shoppingCart.isCheckout()) {
                String message = "Shopping cart with ID " + id + " is already checked out.";
                log.warn(message);
                messages.add(message);
                continue;
            }

            Product product = shoppingCart.getProduct();
            int remainingQuantity = product.getQuantity() - shoppingCart.getQuantity();
            product.setQuantity(remainingQuantity);
            Product updatedProduct = inventoryRepository.save(product);

            if (updatedProduct == null) {
                String message = "Error updating product quantity for product ID " + product.getId();
                log.error(message);
                messages.add(message);
                continue;
            }

            BigDecimal getTotalAmount = updatedProduct.getRetailPrice()
                    .multiply(new BigDecimal(shoppingCart.getQuantity()));
            shoppingCart.setCheckout(true);
            shoppingCart.setTotalAmount(getTotalAmount);
            shoppingCartRepository.save(shoppingCart);
            products.add(updatedProduct);

            log.info("Checked out product ID {}. Remaining quantity: {}. Total amount: {}", product.getId(), remainingQuantity, getTotalAmount);
        }
        log.info("Checkout complete for shopping cart IDs {}", shoppingCartIds);
        List<Object> result = new ArrayList<>();
        result.add(products);
        result.add(messages);
        return result;
    }
}
