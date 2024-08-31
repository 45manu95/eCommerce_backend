package com.manuel.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review", nullable = false)
    private long ID;

    @Basic
    @Column(name = "description", nullable = false, length = 30)
    private String description;

    @Basic
    @Column(name = "stars", nullable = false)
    private int stars;

    @Column(name="date_review", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_review")
    private User userReview;

    @ManyToOne
    @JoinColumn(name = "reviewed_product", nullable = false)
    private Product reviewedProduct;
}
