package org.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ShoppingCartDto {
    private long shoppingCartId;
    private long productId;
    private int purchaseQuantity;

}
