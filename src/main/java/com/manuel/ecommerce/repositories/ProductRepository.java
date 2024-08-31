package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("select p " +
            "from Product p " +
            "where p.barCode like ?1% and p.visible = true")
    Page<Product> findByBarCodeStartingWith(String barcode, Pageable pageable);

    @Query("select p " +
            "from Product p " +
            "where p.barCode = :barCode and p.visible = true")
    Product findByBarCode (String barCode);

    @Query("select p " +
            "from Product p " +
            "where p.barCode like ?1% and p.visible = false")
    Page<Product> searchDeletedProduct(String barcode, Pageable pageable);

    @Query("select p " +
            "from Product p " +
            "where (p.quantity >= :quantity or :quantity is null) and " +
            "(p.state = :state or :state is null or :state = '') and " +
            "(p.category.name = :category or :category is null or :category = '') and p.visible = true")
    Page<Product> filterProduct(Integer quantity,String state,String category, Pageable pageable);

    @Query("select p " +
            "from Product p " +
            "where p.visible = false")
    Page<Product> findDeletedProduct(Pageable pageable);

    @Query("select p " +
            "from Product p " +
            "where p.visible = true ")
    Page<Product> findAllBy(Pageable pageable);

    @Query("select p " +
            "from Product p " +
            "where p.name like ?1% and p.visible = true")
    Page<Product> findByNameStartingWith(String name, Pageable pageable);

    @Query("select AVG(r.stars) " +
            "from Review r " +
            "where r.reviewedProduct.barCode = :barCode")
    double avgReviewProduct(String barCode);

    @Query("select p " +
            "from Product p " +
            "where p.category.name = :categoria and p.visible = true ")
    List<Product> findByCategory(String categoria);

    @Query("select r " +
            "from Review r,Product p " +
            "where p = :prodotto and r.reviewedProduct= p and p.visible = true")
    Page<Review> findProductReviews(Product prodotto, Pageable pageable);


}
