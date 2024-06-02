package org.example.microservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShoppingCartDto {
    private long shoppingCartId;
    private long productId;
    private int purchaseQuantity;

}
