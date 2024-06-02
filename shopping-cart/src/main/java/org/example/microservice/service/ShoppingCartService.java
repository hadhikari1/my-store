package org.example.microservice.service;

import lombok.RequiredArgsConstructor;
import org.example.microservice.dto.ShoppingCartDto;
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
public class ShoppingCartService {
    private final InventoryRepository inventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCart addToCart(ShoppingCartDto shoppingCartDto){
       try{
           Product product = inventoryRepository.findProductById(shoppingCartDto.getProductId());
           if(product != null && addItems(shoppingCartDto.getPurchaseQuantity(), product.getQuantity())){
               ShoppingCart shoppingCart = shoppingCartRepository.getById(shoppingCartDto.getShoppingCartId());
               if(shoppingCart != null && shoppingCartDto.getShoppingCartId() > 0l && !shoppingCart.isCheckout()){
                   shoppingCart.setQuantity(shoppingCartDto.getPurchaseQuantity());
                   return shoppingCartRepository.save(shoppingCart);
               }
               ShoppingCart newShoppingCart = ShoppingCart
                       .builder()
                       .product(product)
                       .quantity(shoppingCartDto.getPurchaseQuantity())
                       .isCheckout(false)
                       .build();
               return shoppingCartRepository.save(newShoppingCart);
           }
       }catch(Exception e){
           System.out.println(e.getMessage());
       }
        return null;
    }
    public boolean addItems(int purchaseQuantity, int productQuantity){
        if(purchaseQuantity > productQuantity){
            return false;
        }
        return true;
    }



    public BigDecimal getTotal(List<Long> shoppingCartIds){
        BigDecimal total = BigDecimal.ZERO;
        try{
            for(Long id: shoppingCartIds){
                ShoppingCart shoppingCart = shoppingCartRepository.getById(id);
                if(shoppingCart != null && !shoppingCart.isCheckout()){
                    Product product = shoppingCart.getProduct();
                    total = product.getRetailPrice().multiply(new BigDecimal(shoppingCart.getQuantity()));
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return total;
    }

    public List<Product> checkout(List<Long> shoppingCartIds){
        try{
            List<Product> products = new ArrayList<>();
            for(Long id: shoppingCartIds){
                ShoppingCart shoppingCart = shoppingCartRepository.getById(id);
                if(shoppingCart == null){
                    break;
                }
                Product product = shoppingCart.getProduct();
                int remainingQuantity = product.getQuantity() - shoppingCart.getQuantity();
                product.setQuantity(remainingQuantity);
                Product updatedProduct = inventoryRepository.save(product);
                if(updatedProduct == null){
                    break;
                }
                shoppingCart.setCheckout(true);
                shoppingCartRepository.save(shoppingCart);
                products.add(updatedProduct);
            }
            return products;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
