package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select c.cart " +
            "from User c " +
            "where c.CF = :user")
    Cart findCart(String user);
}
