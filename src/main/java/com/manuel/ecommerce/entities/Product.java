package com.manuel.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

@Data
@Entity
@Table(name="product")
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private String barCode;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @Basic
    @Column(name = "brand", nullable = false)
    private String brand;

    @Basic
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Basic
    @Column(name = "description", nullable = false)
    private String description;

    @Basic
    @Column(name="state", nullable = false)
    private String state;

    @Basic
    @Column(name="visible", nullable = false)
    private boolean visible;

    @Basic
    @Column(name = "price", nullable = false)
    private double price;

    @Basic
    @Column(name = "average_review")
    private double avgReview;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "product", fetch = FetchType.LAZY)
    private Collection<Composition> cartComposition = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "product", fetch = FetchType.LAZY)
    private Collection<Associated> orderAssociated = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "reviewedProduct", fetch = FetchType.LAZY)
    private Collection<Review> reviews = new LinkedList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "product_images", joinColumns = {@JoinColumn(name = "product_id")}, inverseJoinColumns = {@JoinColumn(name = "image_id")})
    private Set<Image> productImages;

    @Override
    public String toString() {
        return "Prodotto{" +
                "barCode='" + barCode + '\'' +
                ", nome='" + name + '\'' +
                ", marca='" + brand + '\'' +
                ", quantita=" + quantity +
                ", descrizione='" + description + '\'' +
                ", stato='" + state + '\'' +
                ", visibilità=" + visible +
                ", prezzo=" + price +
                ", avgValutazione=" + avgReview +
                '}';
    }

}
