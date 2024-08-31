package com.manuel.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Data
@Entity
@Table(name="order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "purchase_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    @ManyToOne
    @JoinColumn(name = "user_order")
    private User userOrder;

    @Column
    private String state;

    @Basic
    @Column(name = "total_price", nullable = false, length = 30)
    private double totalPrice;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "order", fetch = FetchType.LAZY)
    private Collection<Associated> composizioneOrdine = new LinkedList<>();

    @OneToOne(mappedBy = "order")
    @JoinColumn()
    @JsonIgnore
    private OrderReturn orderReturn;

    @Override
    public String toString() {
        return "Ordine{" +
                "id=" + id +
                ", stato='" + state + '\'' +
                ", data=" + purchaseDate +
                ", totale=" + totalPrice +
                '}';
    }
}
