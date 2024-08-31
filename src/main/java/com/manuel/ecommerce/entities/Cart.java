package com.manuel.ecommerce.entities;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.LinkedList;

@Data
@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private long id;

    @Basic
    @Column(name="total_price", nullable = false)
    private double totalPrice;

    @OneToOne(mappedBy = "cart")
    @JoinColumn
    @JsonIgnore
    private User user;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "cart", fetch = FetchType.LAZY)
    private Collection<Composition> composizioneCarrello = new LinkedList<>();
}
