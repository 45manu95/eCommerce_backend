package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Cart;
import com.manuel.ecommerce.entities.Composition;
import com.manuel.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompositionRepository extends JpaRepository<Composition, Long> {
    boolean existsByProductAndCart(Product product, Cart cart);

}
