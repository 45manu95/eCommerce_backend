package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Composition;
import com.manuel.ecommerce.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Page<User> findByCFStartingWith(String cf, Pageable pageable);

    @Query("select c " +
            "from User c " +
            "where c.visible = true ")
    Page<User> findAll(Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("select c.visible " +
            "from User c " +
            "where c.username = :username")
    boolean findByUsernameIsVisible(String username);

    @Query("select c " +
            "from User c " +
            "where (c.name = :name or :name is null or :name = '') and " +
            "(c.surname = :surname or :surname is null or :surname = '') and " +
            "(c.city = :city or :city is null or :city = '') and " +
            "(c.address = :address or :address is null or :address = '') and " +
            "(c.cap = :cap or :cap is null) and c.visible = true")
    Page<User> filterUser(String name, String surname, String city, String address, Integer cap, Pageable pageable);

    @Query("select comp " +
            "from Cart c, Composition comp " +
            "where c.user = :user and c = comp.cart and c.user.visible = true " +
            "ORDER BY comp.id")
    List<Composition> findProductsCart(User user);

    boolean existsByCellularNumber(int cellularNumber);

    @Query("select c.visible " +
            "from User c " +
            "where c.username = :username")
    boolean findByUsernameIsVisibile(String username);

    @Query("select c " +
            "from User c " +
            "where c.visible = false ")
    Page<User> findDeletedUsers(Pageable pageable);

    @Query("select c " +
            "from User c " +
            "where c.CF like ?1% and c.visible = false")
    Page<User> searchDeletedUser(String cf, Pageable pageable);

}
