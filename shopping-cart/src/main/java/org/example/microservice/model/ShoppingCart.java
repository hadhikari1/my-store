package org.example.microservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    @JoinColumn(name = "productId", referencedColumnName = "id")
    @ManyToOne
    private Product product;

    boolean isCheckout = false;

    private BigDecimal totalAmount;

}
