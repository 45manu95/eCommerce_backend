package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findById(Long id, Pageable pageable);


    @Query("select o " +
            "from Order o " +
            "where o.id = :id")
    Order findByOrderNumber(Long id);

    Page<Order> findByUserOrder(User user, Pageable pageable);

    @Query("select o " +
            "from Order o " +
            "where (o.state = :state or :state is null or :state = '') and " +
            "((year(o.purchaseDate) = : year ) or :year is null )")
    Page<Order> filterOrder(String state, Integer year, Pageable pageable);

    @Query("select o " +
            "from Order o " +
            "where (o.state = :state or :state is null or :state = '') and " +
            "((year(o.purchaseDate) = : year ) or :year is null ) and o.userOrder.CF = :cf")
    Page<Order> filterOrderUser(String cf, String state, Integer year, Pageable pageable);

    @Query("select comp " +
            "from Cart c, Composition comp " +
            "where c = :cart and comp.cart = c")
    List<Composition> findProductCart(Cart cart);

    @Query("select o " +
            "from Order o " +
            "where o.userOrder.CF = :cf and o.id = :orderNumber")
    Page<Order> findByOrderNumberStartingWith(String cf, long orderNumber, Pageable pageable);

    @Query("select a " +
            "from Associated a " +
            "where a.order.id = :idOrder")
    List<Associated> findOrderProducts(long idOrder);

    @Query("select CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "from Order o " +
            "where o.id = :orderNumber and o.userOrder.CF = :cf")
    boolean existsByUserOrderAndOrderNumber(String cf, Long orderNumber);
}
