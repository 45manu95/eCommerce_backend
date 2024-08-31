package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Order;
import com.manuel.ecommerce.entities.OrderReturn;
import com.manuel.ecommerce.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReturnRepository extends JpaRepository<OrderReturn, Long> {
    Page<OrderReturn> findByUserOrderReturn(User userOrderReturn, Pageable pageable);

    boolean existsByOrder(Order order);

}
