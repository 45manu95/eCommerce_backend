package com.manuel.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="composition_order", uniqueConstraints = @UniqueConstraint(columnNames = {"id_cart", "id_product"}))
public class Composition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_cart")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column
    private double subTotal;
}
