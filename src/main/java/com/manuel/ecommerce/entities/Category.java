package com.manuel.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;

@Data
@Entity
@Table(name="category")
public class Category {
    @Id
    @Column(name="name", nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "category", fetch = FetchType.LAZY)
    private Collection<Product> products = new LinkedList<>();
}
