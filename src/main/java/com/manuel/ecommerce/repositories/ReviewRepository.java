package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Product;
import com.manuel.ecommerce.entities.Review;
import com.manuel.ecommerce.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserReviewAndReviewedProduct(User user, Product product);

    Page<Review> findByUserReview(User cf, Pageable pageable);

}
