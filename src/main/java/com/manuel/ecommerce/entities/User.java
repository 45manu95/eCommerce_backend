package com.manuel.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;

@Data
@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "id")
    private String CF;

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Basic
    @Column(name = "surname", nullable = false, length = 30)
    private String surname;

    @Basic
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Basic
    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Basic
    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Basic
    @Column(name = "cap", nullable = false, length = 50)
    private int cap;

    @Basic
    @Column(name = "cellular_number", unique = true, nullable = false, length = 20)
    private int cellularNumber;

    @Column(name = "email",unique = true, nullable = false, length = 50)
    private String email;

    @Column(name="visible", nullable = false)
    private boolean visible;

    @NotBlank
    @Size(max = 20)
    @Column(name="username", nullable = false)
    private String username;

    @NotBlank
    @Column(name="password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name="role",nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "userOrder", fetch = FetchType.LAZY)
    private Collection<Order> orders = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "userOrderReturn", fetch = FetchType.LAZY)
    private Collection<OrderReturn> orderReturns = new LinkedList<>();

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL} , mappedBy = "userReview", fetch = FetchType.LAZY)
    private Collection<Review> review = new LinkedList<>();

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(nullable = false, unique = true)
    private Cart cart;


    @Override
    public String toString() {
        return "Cliente{" +
                "CF='" + CF + '\'' +
                ", nome='" + name + '\'' +
                ", cognome='" + surname + '\'' +
                ", citta='" + city + '\'' +
                ", provincia='" + region + '\'' +
                ", via='" + address + '\'' +
                ", cap=" + cap +
                ", telefono=" + cellularNumber +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
