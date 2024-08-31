package com.manuel.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="order_return")
public class OrderReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descriptionReturn;

    @OneToOne
    @JoinColumn(name = "id_order",nullable = false, unique = true)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_order_return", nullable = false)
    private User userOrderReturn;
}
