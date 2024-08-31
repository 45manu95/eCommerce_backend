package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Associated;
import com.manuel.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociatedRepository extends JpaRepository<Associated, Long> {

    List<Associated> findByOrder(Order order);

    @Query("select CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "from Associated a, Order o " +
            "where a.product.barCode = :ProductID and a.order = o and o.userOrder.CF = :UserCF")
    boolean isUserProductPurchased(String UserCF, String ProductID);
}
